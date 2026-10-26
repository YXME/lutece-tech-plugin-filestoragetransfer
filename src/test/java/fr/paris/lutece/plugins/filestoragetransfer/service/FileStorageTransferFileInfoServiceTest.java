/*
 * Copyright (c) 2002-2024, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES LOSS OF USE, DATA, OR PROFITS OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.filestoragetransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequestStatus;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Test of the file info service
 */
public class FileStorageTransferFileInfoServiceTest extends LuteceTestCase
{
    private static final String FILE_TITLE = "file-info-test.txt";
    private static final byte [ ] FILE_CONTENT = "file storage transfer".getBytes( StandardCharsets.UTF_8 );

    @Inject
    private FileStorageTransferFileInfoService _fileInfoService;

    @Inject
    private FileService _fileService;

    /**
     * Builds a request between two keys of two providers
     *
     * @param status
     *            the status of the request
     * @return the request
     */
    private static FileStorageTransferRequest newRequest( FileStorageTransferRequestStatus status )
    {
        FileStorageTransferRequest request = new FileStorageTransferRequest( );
        request.setSourceFileKey( "source-key" );
        request.setSourceFileserviceproviderName( "sourceProvider" );
        request.setTargetFileKey( "target-key" );
        request.setTargetFileserviceproviderName( "targetProvider" );
        request.setRequestStatus( status );

        return request;
    }

    /**
     * The file of a done request is on its target, the file of any other request on its source
     */
    @Test
    public void testCurrentFile( )
    {
        FileStorageTransferRequest done = newRequest( FileStorageTransferRequestStatus.STATUS_DONE );
        assertEquals( "target-key", _fileInfoService.getCurrentFileKey( done ), "done: target key" );
        assertEquals( "targetProvider", _fileInfoService.getCurrentFileserviceproviderName( done ), "done: target provider" );

        for ( FileStorageTransferRequestStatus status : new FileStorageTransferRequestStatus [ ] {
                FileStorageTransferRequestStatus.STATUS_TODO, FileStorageTransferRequestStatus.STATUS_FAILED, FileStorageTransferRequestStatus.STATUS_ERROR
        } )
        {
            FileStorageTransferRequest request = newRequest( status );
            assertEquals( "source-key", _fileInfoService.getCurrentFileKey( request ), status + ": source key" );
            assertEquals( "sourceProvider", _fileInfoService.getCurrentFileserviceproviderName( request ), status + ": source provider" );
        }

        FileStorageTransferRequest doneWithoutTarget = newRequest( FileStorageTransferRequestStatus.STATUS_DONE );
        doneWithoutTarget.setTargetFileKey( null );
        assertEquals( "source-key", _fileInfoService.getCurrentFileKey( doneWithoutTarget ), "done without target key: source key" );
    }

    /**
     * A stored file gives its metadata and a download URL; a missing file or an unknown provider gives an unreadable info
     *
     * @throws Exception
     *             if the file cannot be stored
     */
    @Test
    public void testFileInfo( ) throws Exception
    {
        IFileStoreServiceProvider provider = _fileService.getFileStoreServiceProvider( );
        PhysicalFile physicalFile = new PhysicalFile( );
        physicalFile.setValue( FILE_CONTENT );
        File file = new File( );
        file.setTitle( FILE_TITLE );
        file.setMimeType( "text/plain" );
        file.setSize( FILE_CONTENT.length );
        file.setPhysicalFile( physicalFile );
        String strKey = provider.storeFile( file );

        try
        {
            FileStorageTransferRequest request = newRequest( FileStorageTransferRequestStatus.STATUS_TODO );
            request.setSourceFileKey( strKey );
            request.setSourceFileserviceproviderName( provider.getName( ) );

            FileStorageTransferFileInfo fileInfo = _fileInfoService.getFileInfo( request );
            assertTrue( fileInfo.isReadable( ), "stored file readable" );
            assertEquals( FILE_TITLE, fileInfo.getName( ), "name" );
            assertEquals( FILE_CONTENT.length, fileInfo.getSize( ), "size" );
            assertEquals( "text/plain", fileInfo.getMimeType( ), "type" );
            assertTrue( fileInfo.getDownloadUrl( ) != null && !fileInfo.getDownloadUrl( ).isEmpty( ), "download url" );

            request.setSourceFileKey( "999999999" );
            assertFalse( _fileInfoService.getFileInfo( request ).isReadable( ), "missing file unreadable" );

            request.setSourceFileKey( strKey );
            request.setSourceFileserviceproviderName( "noSuchProvider" );
            assertFalse( _fileInfoService.getFileInfo( request ).isReadable( ), "unknown provider unreadable" );
        }
        finally
        {
            provider.delete( strKey );
        }
    }
}
