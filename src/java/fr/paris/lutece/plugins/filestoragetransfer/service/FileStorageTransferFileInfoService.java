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
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.filestoragetransfer.service;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequestStatus;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 * Reads, from its current file store provider, what the back office shows of the file of a transfer request
 */
@ApplicationScoped
public class FileStorageTransferFileInfoService
{
    @Inject
    private FileService _fileService;

    @Inject
    @Any
    private Instance<IFileStoreServiceProvider> _fileStoreServiceProviders;

    /**
     * Returns the current key of the file of a request: the target key once the transfer is done, the source key otherwise
     *
     * @param request
     *            the transfer request
     * @return the current key of the file
     */
    public String getCurrentFileKey( FileStorageTransferRequest request )
    {
        return isTransferred( request ) ? request.getTargetFileKey( ) : request.getSourceFileKey( );
    }

    /**
     * Returns the name of the file store provider that currently holds the file of a request
     *
     * @param request
     *            the transfer request
     * @return the provider name
     */
    public String getCurrentFileserviceproviderName( FileStorageTransferRequest request )
    {
        return isTransferred( request ) ? request.getTargetFileserviceproviderName( ) : request.getSourceFileserviceproviderName( );
    }

    /**
     * Reads the metadata and the download URL of the file of a request; a file that cannot be read gives an info marked unreadable
     *
     * @param request
     *            the transfer request
     * @return the file info
     */
    public FileStorageTransferFileInfo getFileInfo( FileStorageTransferRequest request )
    {
        String strFileKey = getCurrentFileKey( request );
        FileStorageTransferFileInfo fileInfo = new FileStorageTransferFileInfo( strFileKey, getCurrentFileserviceproviderName( request ) );

        if ( StringUtils.isBlank( strFileKey ) )
        {
            return fileInfo;
        }

        try
        {
            Optional<IFileStoreServiceProvider> optProvider = findProvider( fileInfo.getFileserviceproviderName( ) );

            if ( optProvider.isEmpty( ) )
            {
                AppLogService.debug( "File {}: provider {} is not deployed", strFileKey, fileInfo.getFileserviceproviderName( ) );

                return fileInfo;
            }

            IFileStoreServiceProvider provider = optProvider.get( );
            File file = provider.getFileMetaData( strFileKey );

            if ( file != null )
            {
                fileInfo.setName( file.getTitle( ) );
                fileInfo.setSize( file.getSize( ) );
                fileInfo.setMimeType( file.getMimeType( ) );
                fileInfo.setDownloadUrl( provider.getFileDownloadUrlBO( strFileKey ) );
                fileInfo.setReadable( true );
            }
        }
        catch( FileServiceException | RuntimeException e )
        {
            AppLogService.debug( "File {} of provider {} cannot be read: {}", strFileKey, fileInfo.getFileserviceproviderName( ), e.getMessage( ) );
        }

        return fileInfo;
    }

    /**
     * Finds a deployed provider by its name without FileService.getFileStoreServiceProvider( name ), which logs a critical AppException for a name no
     * provider answers (a provider removed or renamed since the request was stored); a blank name gives the default provider
     *
     * @param strProviderName
     *            the technical name of the provider
     * @return the provider, empty when none of that name is deployed
     */
    private Optional<IFileStoreServiceProvider> findProvider( String strProviderName )
    {
        if ( StringUtils.isBlank( strProviderName ) )
        {
            return Optional.of( _fileService.getFileStoreServiceProvider( ) );
        }

        return _fileStoreServiceProviders.stream( ).filter( provider -> strProviderName.equals( provider.getName( ) ) ).findFirst( );
    }

    /**
     * Tells whether the file of a request is on its target provider
     *
     * @param request
     *            the transfer request
     * @return true when the transfer is done and the target key is known
     */
    private static boolean isTransferred( FileStorageTransferRequest request )
    {
        return FileStorageTransferRequestStatus.STATUS_DONE.equals( request.getRequestStatus( ) ) && StringUtils.isNotBlank( request.getTargetFileKey( ) );
    }
}
