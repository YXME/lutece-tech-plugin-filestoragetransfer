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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferError;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferErrorHome;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequestHome;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequestStatus;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FileStorageTransferService
{
    private static final String TEMPLATE_MAIL = "admin/plugins/filestoragetransfer/mail/mail_filestoragetransfer.html";

    private static final String MARK_REQUEST = "request";
    private static final String MARK_ERROR = "error";

    @Inject
    @ConfigProperty( name = "filestoragetransfer.RetryDelay", defaultValue = "3600" )
    private int _nRetryDelay;

    @Inject
    @ConfigProperty( name = "filestoragetransfer.RetryLimit", defaultValue = "0" )
    private int _nRetryLimit;

    @Inject
    @ConfigProperty( name = "filestoragetransfer.NotificationsEnabled", defaultValue = "false" )
    private boolean _bNotificationsEnabled;

    @Inject
    @ConfigProperty( name = "filestoragetransfer.MailSubject" )
    private Optional<String> _sMailSubject;

    @Inject
    @ConfigProperty( name = "filestoragetransfer.MailSender" )
    private Optional<String> _sMailSender;

    @Inject
    @ConfigProperty( name = "filestoragetransfer.MailRecipient" )
    private Optional<String> _sMailRecipient;

    @Inject
    @ConfigProperty( name = "lutece.code" )
    private Optional<String> _sApplicationCode;

    @Inject
    @ConfigProperty( name = "lutece.name" )
    private Optional<String> _sSiteName;

    @Inject
    private FileService _fileService;

    @Inject
    private FileStorageTransferNotifierService _fileStorageTransferNotifierService;

    public void TransferFileToTargetFileService( FileStorageTransferRequest request )
    {
        try
        {
            IFileStoreServiceProvider sourceFileStoreService = _fileService.getFileStoreServiceProvider( request.getSourceFileserviceproviderName( ) );
            IFileStoreServiceProvider targetFileStoreService = _fileService.getFileStoreServiceProvider( request.getTargetFileserviceproviderName( ) );

            String targetFileKey = sendFileToTargetFileService( request.getSourceFileKey( ), sourceFileStoreService,
                    targetFileStoreService );

            request.setTargetFileKey( targetFileKey );
            FileStorageTransferRequestHome.update( request );

            _fileStorageTransferNotifierService.notifyFileTransferListeners( request );

            deleteFileFromSourceFileService( request.getSourceFileKey( ), sourceFileStoreService );

            request.setRequestStatus( FileStorageTransferRequestStatus.STATUS_DONE );
            FileStorageTransferRequestHome.update( request );
        
        }
        catch( FileServiceException e )
        {
            HandleException( request, e, e.getResponseCode( ) != null ? e.getResponseCode( ) : 404 );
        }
        catch( IllegalStateException e )
        {
            HandleException( request, e, 520 );
        }
        catch( Exception e )
        {
            HandleException( request, e, 500 );
        }
    }

    private String sendFileToTargetFileService( String strSourceFileKey, IFileStoreServiceProvider sourceFileStoreService, IFileStoreServiceProvider targetFileStoreService )
            throws FileServiceException
    {
        File fileToTransfer = sourceFileStoreService.getFile( strSourceFileKey );

        if ( fileToTransfer == null )
        {
            throw new FileServiceException( "File not found", 404, null );
        }

        String strTargetFileKey = targetFileStoreService.storeFile( fileToTransfer );
        return strTargetFileKey;

    }

    private void deleteFileFromSourceFileService( String strSourceFileKey, IFileStoreServiceProvider sourceFileStoreService ) throws FileServiceException
    {
        if ( sourceFileStoreService == null )
        {
            throw new FileServiceException( "Source file service provider not found", 404, null );
        }

        sourceFileStoreService.delete( strSourceFileKey );
    }

    private void deleteFileFromTargetFileService( String strTargetFileKey, IFileStoreServiceProvider targetFileStoreService ) throws FileServiceException
    {
        if ( targetFileStoreService == null )
        {
            throw new FileServiceException( "Target file service provider not found", 404, null );
        }
        targetFileStoreService.delete( strTargetFileKey );
    }

    private void HandleException( FileStorageTransferRequest request, Exception e, int ResponseCode )
    {
        request.upRetryCount( );

        if ( request.getRetryCount( ) >= _nRetryLimit )
        {
            request.setRequestStatus( FileStorageTransferRequestStatus.STATUS_ERROR );
        }
        else
        {
            request.setRequestStatus( FileStorageTransferRequestStatus.STATUS_FAILED );
        }

        request.setExecutionTime( Timestamp.from( Instant.now( ).plusSeconds( _nRetryDelay ) ) );
        FileStorageTransferRequestHome.update( request );

        StringWriter sw = new StringWriter( );
        e.printStackTrace( new PrintWriter( sw ) );

        FileStorageTransferError error = new FileStorageTransferError( request.getId( ), ResponseCode, e.getMessage( ) == null ? "" : e.getMessage( ), sw.toString( ),
                Timestamp.from( Instant.now( ) ) );
        FileStorageTransferErrorHome.create( error );

        if ( _bNotificationsEnabled )
        {
            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_REQUEST, request );
            model.put( MARK_ERROR, error );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MAIL, null, model );
            String strSenderName = _sApplicationCode.orElse( "" ) + " " + _sSiteName.orElse( "" );
            if ( StringUtils.isBlank( request.getContactMail( ) ) )
            {
                MailService.sendMailHtml( _sMailRecipient.orElse( null ), strSenderName, _sMailSender.orElse( null ), _sMailSubject.orElse( "" ), template.getHtml( ) );
            }
            else
            {
                MailService.sendMailHtml( request.getContactMail( ), null, _sMailRecipient.orElse( null ), strSenderName, _sMailSender.orElse( null ),
                        _sMailSubject.orElse( "" ), template.getHtml( ) );
            }
        }
    }

    public Timestamp getExecutionTime( )
    {
        return Timestamp.from( Instant.now( ) );
    }

    public Timestamp getDelayedExecutionTime( long retryDelay )
    {
        return Timestamp.from( Instant.now( ).plusSeconds( retryDelay ) );
    }

}
