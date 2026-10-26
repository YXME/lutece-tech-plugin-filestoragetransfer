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
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class provides the user interface to manage Error features ( manage, view )
 */
@SessionScoped
@Named
@Controller( controllerJsp = "ManageErrors.jsp", controllerPath = "jsp/admin/plugins/filestoragetransfer/", right = "FILESTORAGETRANSFER_ERROR_MANAGEMENT", securityTokenEnabled = true )
public class FileStorageTransferErrorJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = 1L;

    // Rights
    public static final String RIGHT_MANAGETRANSFERERROR = "FILESTORAGETRANSFER_ERROR_MANAGEMENT";

    // Templates
    private static final String TEMPLATE_MANAGE_ERRORS = "/admin/plugins/filestoragetransfer/manage_errors.html";
    private static final String TEMPLATE_VIEW_ERROR = "/admin/plugins/filestoragetransfer/view_error.html";

    // Parameters
    private static final String PARAMETER_ID_ERROR = "id";

    // Properties
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "filestoragetransfer.listItems.itemsPerPage";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ERRORS = "filestoragetransfer.manage_errors.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_VIEW_ERROR = "filestoragetransfer.view_error.pageTitle";

    // Markers
    private static final String MARK_ERROR_LIST = "error_list";
    private static final String MARK_ERROR = "error";

    private static final String JSP_MANAGE_ERRORS = "jsp/admin/plugins/filestoragetransfer/ManageErrors.jsp";

    // Views
    private static final String VIEW_MANAGE_ERRORS = "manageErrors";
    private static final String VIEW_ERROR = "viewError";

    // Errors
    private static final String MESSAGE_ERROR_ERROR_NOT_FOUND = "filestoragetransfer.message.error.errorNotFound";

    @Inject
    @Pager( name = "filestoragetransfer.errors", listBookmark = MARK_ERROR_LIST, defaultItemsPerPage = PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, baseUrl = JSP_MANAGE_ERRORS )
    private IPager<Integer, FileStorageTransferError> _pager;

    // Session variable to store working values
    private FileStorageTransferError _error;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @param model
     *            The model
     * @return The page
     */
    @View( value = VIEW_MANAGE_ERRORS, defaultView = true )
    public String getManageErrors( HttpServletRequest request, Models model )
    {
        _error = null;

        _pager.withIdList( FileStorageTransferErrorHome.getIdErrorsList( ) ).populateModels( request, model, FileStorageTransferErrorJspBean::getItemsFromIds,
                getLocale( ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_ERRORS, TEMPLATE_MANAGE_ERRORS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     *            the list of error ids of the page
     * @return the populated list of items corresponding to the id List
     */
    private static List<FileStorageTransferError> getItemsFromIds( List<Integer> listIds )
    {
        List<FileStorageTransferError> listError = FileStorageTransferErrorHome.getErrorsListByIds( listIds );

        return listError.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * Returns the detail of an error
     *
     * @param request
     *            The Http request
     * @param model
     *            The model
     * @return The HTML detail of the error
     */
    @View( VIEW_ERROR )
    public String getError( HttpServletRequest request, Models model )
    {
        int nId = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ERROR ), -1 );

        if ( _error == null || ( _error.getId( ) != nId ) )
        {
            Optional<FileStorageTransferError> optError = FileStorageTransferErrorHome.findByPrimaryKey( nId );
            _error = optError.orElse( null );
        }

        if ( _error == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_ERROR_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        model.put( MARK_ERROR, _error );

        return getPage( PROPERTY_PAGE_TITLE_VIEW_ERROR, TEMPLATE_VIEW_ERROR, model );
    }
}
