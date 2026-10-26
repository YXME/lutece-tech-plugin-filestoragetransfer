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
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *"
 * License 1.0
 */

package fr.paris.lutece.plugins.filestoragetransfer.business;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequestHome;
import fr.paris.lutece.test.LuteceTestCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

/**
 * This is the business class test for the object Request
 */
public class RequestBusinessTest extends LuteceTestCase
{
    private static final String SOURCEFILEKEY1 = "SourceFileKey1";
    private static final String SOURCEFILEKEY2 = "SourceFileKey2";
    private static final String SOURCEFILESERVICEPROVIDERNAME1 = "SourceFileserviceproviderName1";
    private static final String SOURCEFILESERVICEPROVIDERNAME2 = "SourceFileserviceproviderName2";
    private static final String TARGETFILESERVICEPROVIDERNAME1 = "TargetFileserviceproviderName1";
    private static final String TARGETFILESERVICEPROVIDERNAME2 = "TargetFileserviceproviderName2";

    /**
     * test Request
     */
    @Test
    public void testBusiness( )
    {
        // Initialize an object
        FileStorageTransferRequest request = new FileStorageTransferRequest( );
        request.setSourceFileKey( SOURCEFILEKEY1 );
        request.setSourceFileserviceproviderName( SOURCEFILESERVICEPROVIDERNAME1 );
        request.setTargetFileserviceproviderName( TARGETFILESERVICEPROVIDERNAME1 );
        request.setRequestStatus( FileStorageTransferRequestStatus.STATUS_TODO );
        request.setExecutionTime( Timestamp.from( Instant.now( ) ) );
        request.setCreationTime( Timestamp.from( Instant.now( ) ) );

        int nTodoBefore = FileStorageTransferRequestHome.countByStatus( ).getOrDefault( FileStorageTransferRequestStatus.STATUS_TODO.getValue( ), 0 );

        // Create test
        FileStorageTransferRequestHome.create( request );
        assertEquals( nTodoBefore + 1, FileStorageTransferRequestHome.countByStatus( ).get( FileStorageTransferRequestStatus.STATUS_TODO.getValue( ) ) );
        Optional<FileStorageTransferRequest> optRequestStored = FileStorageTransferRequestHome.findByPrimaryKey( request.getId( ) );
        FileStorageTransferRequest requestStored = optRequestStored.orElse( new FileStorageTransferRequest( ) );
        assertEquals( requestStored.getSourceFileKey( ), request.getSourceFileKey( ) );
        assertEquals( requestStored.getSourceFileserviceproviderName( ), request.getSourceFileserviceproviderName( ) );
        assertEquals( requestStored.getTargetFileserviceproviderName( ), request.getTargetFileserviceproviderName( ) );
        assertEquals( requestStored.getRequestStatus( ), request.getRequestStatus( ) );

        // Update test
        request.setSourceFileKey( SOURCEFILEKEY2 );
        request.setSourceFileserviceproviderName( SOURCEFILESERVICEPROVIDERNAME2 );
        request.setTargetFileserviceproviderName( TARGETFILESERVICEPROVIDERNAME2 );
        request.setRequestStatus( FileStorageTransferRequestStatus.STATUS_DONE );
        FileStorageTransferRequestHome.update( request );
        optRequestStored = FileStorageTransferRequestHome.findByPrimaryKey( request.getId( ) );
        requestStored = optRequestStored.orElse( new FileStorageTransferRequest( ) );

        assertEquals( requestStored.getSourceFileKey( ), request.getSourceFileKey( ) );
        assertEquals( requestStored.getSourceFileserviceproviderName( ), request.getSourceFileserviceproviderName( ) );
        assertEquals( requestStored.getTargetFileserviceproviderName( ), request.getTargetFileserviceproviderName( ) );
        assertEquals( requestStored.getRequestStatus( ), request.getRequestStatus( ) );

        // List test
        FileStorageTransferRequestHome.getRequestsList( );

        // Delete test
        FileStorageTransferRequestHome.remove( request.getId( ) );
        optRequestStored = FileStorageTransferRequestHome.findByPrimaryKey( request.getId( ) );
        requestStored = optRequestStored.orElse( null );
        assertNull( requestStored );

    }

}
