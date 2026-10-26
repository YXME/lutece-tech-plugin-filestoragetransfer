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

package fr.paris.lutece.plugins.filestoragetransfer.web;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferError;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferErrorHome;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequestHome;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequestStatus;
import fr.paris.lutece.plugins.filestoragetransfer.service.FileStorageTransferFileInfo;
import fr.paris.lutece.plugins.filestoragetransfer.service.FileStorageTransferFileInfoService;
import fr.paris.lutece.plugins.filestoragetransfer.service.FileStorageTransferService;
import fr.paris.lutece.plugins.filestoragetransfer.service.FileStoreProviderLabelService;
import fr.paris.lutece.plugins.filestoragetransfer.service.resource.FileStorageTransferResourceLink;
import fr.paris.lutece.plugins.filestoragetransfer.service.resource.FileStorageTransferResourceService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.url.UrlItem;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class provides the user interface to manage Request features ( manage, create, modify, remove )
 */
@SessionScoped
@Named
@Controller( controllerJsp = "ManageRequests.jsp", controllerPath = "jsp/admin/plugins/filestoragetransfer/", right = "FILESTORAGETRANSFER_REQUEST_MANAGEMENT", securityTokenEnabled = true )
public class FileStorageTransferRequestJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = 1L;

    // Rights
    public static final String RIGHT_MANAGETRANSFERREQUEST = "FILESTORAGETRANSFER_REQUEST_MANAGEMENT";
    public static final String RIGHT_CREATETRANSFERREQUEST = "FILESTORAGETRANSFER_REQUEST_CREATION";

    // Templates
    private static final String TEMPLATE_MANAGE_REQUESTS = "/admin/plugins/filestoragetransfer/manage_requests.html";
    private static final String TEMPLATE_CREATE_REQUEST = "/admin/plugins/filestoragetransfer/create_request.html";
    private static final String TEMPLATE_MODIFY_REQUEST = "/admin/plugins/filestoragetransfer/modify_request.html";
    private static final String TEMPLATE_VIEW_REQUEST = "/admin/plugins/filestoragetransfer/view_request.html";

    // Parameters
    private static final String PARAMETER_ID_REQUEST = "id";
    private static final String PARAMETER_STATUS = "status";

    // Properties
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "filestoragetransfer.listItems.itemsPerPage";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_REQUESTS = "filestoragetransfer.manage_requests.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_REQUEST = "filestoragetransfer.modify_request.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_REQUEST = "filestoragetransfer.create_request.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_VIEW_REQUEST = "filestoragetransfer.view_request.pageTitle";

    // Markers
    private static final String MARK_REQUEST_LIST = "request_list";
    private static final String MARK_REQUEST = "request";
    private static final String MARK_FILESERVICES_LIST = "fileservice_list";
    private static final String MARK_ERROR_LIST = "error_list";
    private static final String MARK_REQUEST_STATUS_LIST = "request_status_list";
    private static final String MARK_SELECTED_REQUEST_STATUS_FILTER = "selected_request_status_filter";
    private static final String MARK_COUNT_BY_STATUS = "count_by_status";
    private static final String MARK_COUNT_ALL = "count_all";
    private static final String MARK_FILE_INFOS = "file_infos";
    private static final String MARK_RESOURCE_LINKS = "resource_links";
    private static final String MARK_FILE_INFO = "file_info";
    private static final String MARK_PROVIDER_LABELS = "provider_labels";
    private static final String MARK_RESOURCE_LINK = "resource_link";
    private static final String MARK_LOCALE = "locale";

    private static final String JSP_MANAGE_REQUESTS = "jsp/admin/plugins/filestoragetransfer/ManageRequests.jsp";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_REQUEST = "filestoragetransfer.message.confirmRemoveRequest";
    private static final String MESSAGE_CONFIRM_RESET_FILE_TRANSFER_REQUEST_STATUS = "filestoragetransfer.message.confirmResetFileRequestStatus";
    private static final String MESSAGE_CONFIRM_PLAY_FILE_TRANSFER_REQUEST = "filestoragetransfer.message.confirmPlayFileTransferRequest";
    private static final String MESSAGE_CONFIRM_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS = "filestoragetransfer.message.confirmResetStatusOfAllErrorRequests";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "filestoragetransfer.model.entity.request.attribute.";

    // Views
    private static final String VIEW_MANAGE_REQUESTS = "manageRequests";
    private static final String VIEW_CREATE_REQUEST = "createRequest";
    private static final String VIEW_MODIFY_REQUEST = "modifyRequest";
    private static final String VIEW_VIEW_REQUEST = "viewRequest";
    private static final String VIEW_CONFIRM_REMOVE_REQUEST = "confirmRemoveRequest";
    private static final String VIEW_CONFIRM_RESET_FILE_TRANSFER_REQUEST_STATUS = "confirmResetFileRequestStatus";
    private static final String VIEW_CONFIRM_PLAY_FILE_TRANSFER_REQUEST = "confirmPlayFileTransferRequest";
    private static final String VIEW_CONFIRM_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS = "confirmResetStatusOfAllErrorRequests";

    // Actions
    private static final String ACTION_CREATE_REQUEST = VIEW_CREATE_REQUEST;
    private static final String ACTION_MODIFY_REQUEST = VIEW_MODIFY_REQUEST;
    private static final String ACTION_REMOVE_REQUEST = "removeRequest";
    private static final String ACTION_RESET_FILE_TRANSFER_REQUEST_STATUS = "resetFileRequestStatus";
    private static final String ACTION_PLAY_FILE_TRANSFER_REQUEST = "playFileTransferRequest";
    private static final String ACTION_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS = "resetStatusOfAllErrorRequests";

    // Infos
    private static final String INFO_REQUEST_CREATED = "filestoragetransfer.info.request.created";
    private static final String INFO_REQUEST_UPDATED = "filestoragetransfer.info.request.updated";
    private static final String INFO_REQUEST_REMOVED = "filestoragetransfer.info.request.removed";
    private static final String INFO_RESET_FILE_TRANSFER_REQUEST_STATUS = "filestoragetransfer.info.request.status.reset";
    private static final String INFO_REQUEST_PLAYED = "filestoragetransfer.info.request.played";
    private static final String INFO_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS = "filestoragetransfer.info.request.status.reset.all";

    // Errors
    private static final String MESSAGE_ERROR_REQUEST_NOT_FOUND = "filestoragetransfer.message.error.requestNotFound";
    private static final String MESSAGE_ERROR_REQUEST_PLAY_FAILED = "filestoragetransfer.message.error.requestPlayFailed";

    @Inject
    @Pager( name = "filestoragetransfer.requests", listBookmark = MARK_REQUEST_LIST, defaultItemsPerPage = PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, baseUrl = JSP_MANAGE_REQUESTS )
    private IPager<Integer, FileStorageTransferRequest> _pager;

    @Inject
    private FileStorageTransferService _fileStorageTransferService;

    @Inject
    private FileStorageTransferFileInfoService _fileInfoService;

    @Inject
    private FileStorageTransferResourceService _resourceService;

    @Inject
    private FileStoreProviderLabelService _providerLabelService;

    // Session variable to store working values
    private FileStorageTransferRequest _fileTransferRequest;
    private FileStorageTransferRequestStatus _statusFilter = FileStorageTransferRequestStatus.STATUS_UNKNOWN;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @param model
     *            The model
     * @return The page
     */
    @View( value = VIEW_MANAGE_REQUESTS, defaultView = true )
    public String getManageRequests( HttpServletRequest request, Models model )
    {
        _fileTransferRequest = null;

        String strStatus = request.getParameter( PARAMETER_STATUS );

        if ( strStatus != null )
        {
            _statusFilter = FileStorageTransferRequestStatus.getRequestStatusByValue( strStatus );
        }

        List<Integer> listIdFileTransferRequests;

        if ( !_statusFilter.equals( FileStorageTransferRequestStatus.STATUS_UNKNOWN ) )
        {
            listIdFileTransferRequests = FileStorageTransferRequestHome.getIdRequestsListByStatus( _statusFilter.getValue( ) );
        }
        else
        {
            listIdFileTransferRequests = FileStorageTransferRequestHome.getIdRequestsList( );
        }

        UrlItem urlPager = new UrlItem( JSP_MANAGE_REQUESTS );
        urlPager.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_REQUESTS );
        urlPager.addParameter( PARAMETER_STATUS, _statusFilter.getValue( ) );

        _pager.withBaseUrl( urlPager.getUrl( ) ).withIdList( listIdFileTransferRequests ).populateModels( request, model,
                FileStorageTransferRequestJspBean::getItemsFromIds, getLocale( ) );

        @SuppressWarnings( "unchecked" )
        List<FileStorageTransferRequest> listPageRequests = (List<FileStorageTransferRequest>) model.get( MARK_REQUEST_LIST );
        Map<String, FileStorageTransferFileInfo> mapFileInfos = new HashMap<>( );
        listPageRequests.forEach( fileRequest -> mapFileInfos.put( String.valueOf( fileRequest.getId( ) ), _fileInfoService.getFileInfo( fileRequest ) ) );
        Map<String, FileStorageTransferResourceLink> mapResourceLinks = new HashMap<>( );
        _resourceService.getResourceLinks( listPageRequests, getLocale( ) ).forEach( ( nId, link ) -> mapResourceLinks.put( String.valueOf( nId ), link ) );

        Map<String, Integer> mapCountByStatus = FileStorageTransferRequestHome.countByStatus( );

        model.put( MARK_REQUEST_STATUS_LIST, FileStorageTransferRequestStatus.getAllStatus( ) );
        model.put( MARK_SELECTED_REQUEST_STATUS_FILTER, _statusFilter );
        model.put( MARK_COUNT_BY_STATUS, mapCountByStatus );
        model.put( MARK_COUNT_ALL, mapCountByStatus.values( ).stream( ).mapToInt( Integer::intValue ).sum( ) );
        model.put( MARK_FILE_INFOS, mapFileInfos );
        model.put( MARK_RESOURCE_LINKS, mapResourceLinks );
        model.put( MARK_PROVIDER_LABELS, getProviderLabels( listPageRequests ) );
        model.put( MARK_LOCALE, getLocale( ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_REQUESTS, TEMPLATE_MANAGE_REQUESTS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     *            the list of request ids of the page
     * @return the populated list of items corresponding to the id List
     */
    private static List<FileStorageTransferRequest> getItemsFromIds( List<Integer> listIds )
    {
        List<FileStorageTransferRequest> listRequest = FileStorageTransferRequestHome.getRequestsListByIds( listIds );

        return listRequest.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * Returns the labels of the file store service providers of the site and of the providers named by some requests
     *
     * @param listRequests
     *            the requests shown
     * @return the labels by technical name
     */
    private Map<String, String> getProviderLabels( List<FileStorageTransferRequest> listRequests )
    {
        List<String> listNames = new ArrayList<>( );
        listRequests.forEach( fileRequest -> {
            listNames.add( fileRequest.getSourceFileserviceproviderName( ) );
            listNames.add( fileRequest.getTargetFileserviceproviderName( ) );
        } );

        return _providerLabelService.getLabels( listNames, getLocale( ) );
    }

    /**
     * Loads the request of the id parameter into the working request
     *
     * @param request
     *            The Http request
     * @param bReload
     *            true to read the request from the database even when it is already the working one (the daemon may have changed it), false to keep
     *            the working one, which holds the values of a modification form refused by the validation
     * @return true when the id parameter names an existing request, false otherwise (missing, non numeric or unknown id)
     */
    private boolean loadFileTransferRequest( HttpServletRequest request, boolean bReload )
    {
        int nId = NumberUtils.toInt( request.getParameter( PARAMETER_ID_REQUEST ), -1 );

        if ( bReload || _fileTransferRequest == null || ( _fileTransferRequest.getId( ) != nId ) )
        {
            Optional<FileStorageTransferRequest> optRequest = FileStorageTransferRequestHome.findByPrimaryKey( nId );
            _fileTransferRequest = optRequest.orElse( null );
        }

        return _fileTransferRequest != null;
    }

    /**
     * Redirects to the stop message telling that the request of the id parameter cannot be found
     *
     * @param request
     *            The Http request
     * @return the URL of the stop message
     */
    private String redirectToRequestNotFound( HttpServletRequest request )
    {
        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REQUEST_NOT_FOUND, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Returns the form to create a request
     *
     * @param request
     *            The Http request
     * @param model
     *            The model
     * @return the html code of the request form
     */
    @View( VIEW_CREATE_REQUEST )
    public String getCreateRequest( HttpServletRequest request, Models model )
    {
        _fileTransferRequest = ( _fileTransferRequest != null ) ? _fileTransferRequest : new FileStorageTransferRequest( );

        model.put( MARK_REQUEST, _fileTransferRequest );
        model.put( MARK_FILESERVICES_LIST, _providerLabelService.getProviders( getLocale( ) ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_REQUEST, TEMPLATE_CREATE_REQUEST, model );
    }

    /**
     * Returns the detail of a request
     *
     * @param request
     *            The Http request
     * @param model
     *            The model
     * @return the html code of the request detail
     */
    @View( VIEW_VIEW_REQUEST )
    public String getViewRequest( HttpServletRequest request, Models model )
    {
        if ( !loadFileTransferRequest( request, true ) )
        {
            return redirectToRequestNotFound( request );
        }

        List<FileStorageTransferError> errorList = FileStorageTransferErrorHome.getErrorsListByRequestId( _fileTransferRequest.getId( ) );

        model.put( MARK_REQUEST, _fileTransferRequest );
        model.put( MARK_ERROR_LIST, errorList );
        model.put( MARK_FILE_INFO, _fileInfoService.getFileInfo( _fileTransferRequest ) );
        model.put( MARK_PROVIDER_LABELS, getProviderLabels( List.of( _fileTransferRequest ) ) );
        model.put( MARK_RESOURCE_LINK, _resourceService.getResourceLinks( List.of( _fileTransferRequest ), getLocale( ) ).get( _fileTransferRequest.getId( ) ) );

        return getPage( PROPERTY_PAGE_TITLE_VIEW_REQUEST, TEMPLATE_VIEW_REQUEST, model );
    }

    /**
     * Process the data capture form of a new request
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_REQUEST )
    public String doCreateRequest( HttpServletRequest request )
    {
        if ( _fileTransferRequest == null )
        {
            _fileTransferRequest = new FileStorageTransferRequest( );
        }

        populate( _fileTransferRequest, request, getLocale( ) );

        Timestamp currentTime = Timestamp.from( Instant.now( ) );

        _fileTransferRequest.setRequestStatus( FileStorageTransferRequestStatus.STATUS_TODO );
        _fileTransferRequest.setRetryCount( 0 );
        _fileTransferRequest.setCreationTime( currentTime );
        _fileTransferRequest.setExecutionTime( currentTime );

        if ( !validateBean( _fileTransferRequest, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_REQUEST );
        }

        FileStorageTransferRequestHome.create( _fileTransferRequest );
        addInfo( INFO_REQUEST_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_REQUESTS );
    }

    /**
     * Manages the removal form of a request whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @View( value = VIEW_CONFIRM_REMOVE_REQUEST, securityTokenAction = ACTION_REMOVE_REQUEST )
    public String getConfirmRemoveRequest( HttpServletRequest request )
    {
        return redirectToConfirmation( request, ACTION_REMOVE_REQUEST, MESSAGE_CONFIRM_REMOVE_REQUEST );
    }

    /**
     * Handles the removal of a request and of its errors
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage requests
     */
    @Action( ACTION_REMOVE_REQUEST )
    public String doRemoveRequest( HttpServletRequest request )
    {
        if ( !loadFileTransferRequest( request, true ) )
        {
            return redirectToRequestNotFound( request );
        }

        int nId = _fileTransferRequest.getId( );

        FileStorageTransferErrorHome.getErrorsListByRequestId( nId ).forEach( error -> FileStorageTransferErrorHome.remove( error.getId( ) ) );
        FileStorageTransferRequestHome.remove( nId );
        _fileTransferRequest = null;
        addInfo( INFO_REQUEST_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_REQUESTS );
    }

    /**
     * Asks the confirmation to reset the status of a request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @View( value = VIEW_CONFIRM_RESET_FILE_TRANSFER_REQUEST_STATUS, securityTokenAction = ACTION_RESET_FILE_TRANSFER_REQUEST_STATUS )
    public String getConfirmResetFileRequestStatus( HttpServletRequest request )
    {
        return redirectToConfirmation( request, ACTION_RESET_FILE_TRANSFER_REQUEST_STATUS, MESSAGE_CONFIRM_RESET_FILE_TRANSFER_REQUEST_STATUS );
    }

    /**
     * Resets the status of a request so that the daemon executes it again
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage requests
     */
    @Action( ACTION_RESET_FILE_TRANSFER_REQUEST_STATUS )
    public String doResetFileRequestStatus( HttpServletRequest request )
    {
        if ( !loadFileTransferRequest( request, true ) )
        {
            return redirectToRequestNotFound( request );
        }

        _fileTransferRequest.setRequestStatus( FileStorageTransferRequestStatus.STATUS_TODO );
        _fileTransferRequest.setRetryCount( 0 );
        _fileTransferRequest.setExecutionTime( Timestamp.from( Instant.now( ) ) );

        FileStorageTransferRequestHome.update( _fileTransferRequest );
        addInfo( INFO_RESET_FILE_TRANSFER_REQUEST_STATUS, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_REQUESTS );
    }

    /**
     * Asks the confirmation to reset the status of every request in error
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @View( value = VIEW_CONFIRM_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS, securityTokenAction = ACTION_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS )
    public String getConfirmResetAllFileRequestStatus( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( getActionUrl( ACTION_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS ) );

        return redirect( request,
                AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Resets the status of every request in error
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage requests
     */
    @Action( ACTION_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS )
    public String doResetAllFileRequestStatus( HttpServletRequest request )
    {
        List<FileStorageTransferRequest> fileRequestList = FileStorageTransferRequestHome
                .getRequestsListByStatus( FileStorageTransferRequestStatus.STATUS_ERROR.getValue( ) );

        fileRequestList.forEach( fileRequest -> {
            fileRequest.setRequestStatus( FileStorageTransferRequestStatus.STATUS_TODO );
            FileStorageTransferRequestHome.update( fileRequest );
        } );

        addInfo( INFO_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_REQUESTS );
    }

    /**
     * Asks the confirmation to execute a request now
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @View( value = VIEW_CONFIRM_PLAY_FILE_TRANSFER_REQUEST, securityTokenAction = ACTION_PLAY_FILE_TRANSFER_REQUEST )
    public String getConfirmPlayFileTransferRequest( HttpServletRequest request )
    {
        return redirectToConfirmation( request, ACTION_PLAY_FILE_TRANSFER_REQUEST, MESSAGE_CONFIRM_PLAY_FILE_TRANSFER_REQUEST );
    }

    /**
     * Executes a request now
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage requests
     */
    @Action( ACTION_PLAY_FILE_TRANSFER_REQUEST )
    public String doPlayFileTransferRequest( HttpServletRequest request )
    {
        if ( !loadFileTransferRequest( request, true ) )
        {
            return redirectToRequestNotFound( request );
        }

        _fileStorageTransferService.TransferFileToTargetFileService( _fileTransferRequest );

        if ( FileStorageTransferRequestStatus.STATUS_DONE.equals( _fileTransferRequest.getRequestStatus( ) ) )
        {
            addInfo( INFO_REQUEST_PLAYED, getLocale( ) );
        }
        else
        {
            addError( MESSAGE_ERROR_REQUEST_PLAY_FAILED, getLocale( ) );
        }

        return redirectView( request, VIEW_MANAGE_REQUESTS );
    }

    /**
     * Redirects to the confirmation message of an action on the request whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @param strAction
     *            the action the confirmation posts
     * @param strMessageKey
     *            the key of the confirmation message
     * @return the URL of the confirmation message
     */
    private String redirectToConfirmation( HttpServletRequest request, String strAction, String strMessageKey )
    {
        if ( !loadFileTransferRequest( request, true ) )
        {
            return redirectToRequestNotFound( request );
        }

        UrlItem url = new UrlItem( getActionUrl( strAction ) );
        url.addParameter( PARAMETER_ID_REQUEST, _fileTransferRequest.getId( ) );

        return redirect( request, AdminMessageService.getMessageUrl( request, strMessageKey, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Returns the form to update info about a request
     *
     * @param request
     *            The Http request
     * @param model
     *            The model
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_REQUEST )
    public String getModifyRequest( HttpServletRequest request, Models model )
    {
        if ( !loadFileTransferRequest( request, false ) )
        {
            return redirectToRequestNotFound( request );
        }

        model.put( MARK_REQUEST, _fileTransferRequest );
        model.put( MARK_FILESERVICES_LIST, _providerLabelService.getProviders( getLocale( ) ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_REQUEST, TEMPLATE_MODIFY_REQUEST, model );
    }

    /**
     * Process the change form of a request
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_REQUEST )
    public String doModifyRequest( HttpServletRequest request )
    {
        if ( !loadFileTransferRequest( request, true ) )
        {
            return redirectToRequestNotFound( request );
        }

        populate( _fileTransferRequest, request, getLocale( ) );

        if ( !validateBean( _fileTransferRequest, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_REQUEST, PARAMETER_ID_REQUEST, _fileTransferRequest.getId( ) );
        }

        FileStorageTransferRequestHome.update( _fileTransferRequest );
        addInfo( INFO_REQUEST_UPDATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_REQUESTS );
    }
}
