package fr.paris.lutece.plugins.filestoragetransfer.service;

import java.util.stream.Collectors;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.service.listener.IFileStorageTransferListener;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 * Notifies the file storage transfer listeners when a file has been transferred
 */
@ApplicationScoped
public class FileStorageTransferNotifierService
{
    @Inject
    @Any
    private Instance<IFileStorageTransferListener> _fileTransferListeners;

    /**
     * Notifies every file storage transfer listener that the file of the request moved to its target file service
     *
     * @param request
     *            the transfer request
     */
    public void notifyFileTransferListeners( FileStorageTransferRequest request )
    {
        AppLogService.debug( "FileTransferListener - loading listeners  : {}",
                _fileTransferListeners.stream( ).map( IFileStorageTransferListener::getName ).collect( Collectors.joining( ", " ) ) );

        _fileTransferListeners.forEach( listener -> listener.changeFileService( request ) );
    }
}
