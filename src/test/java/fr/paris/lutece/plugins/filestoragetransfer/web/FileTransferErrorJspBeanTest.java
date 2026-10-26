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

package fr.paris.lutece.plugins.filestoragetransfer.web;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Timestamp;
import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferError;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferErrorHome;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequestHome;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequestStatus;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.web.cdi.mvc.ModelsImpl;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.inject.Inject;

/**
 * Test of the error JspBean
 */
public class FileTransferErrorJspBeanTest extends LuteceTestCase
{
    private static final String ERRORMESSAGE1 = "ErrorMessage1";

    @Inject
    private FileStorageTransferErrorJspBean _bean;

    private FileStorageTransferRequest _fileTransferRequest;
    private FileStorageTransferError _error;

    /**
     * Creates a request in error to work on
     *
     * @throws Exception
     *             if the parent setup fails
     */
    @BeforeEach
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        _fileTransferRequest = new FileStorageTransferRequest( );
        _fileTransferRequest.setSourceFileKey( "SourceFileKey1" );
        _fileTransferRequest.setSourceFileserviceproviderName( "SourceFileserviceproviderName1" );
        _fileTransferRequest.setTargetFileserviceproviderName( "TargetFileserviceproviderName1" );
        _fileTransferRequest.setRequestStatus( FileStorageTransferRequestStatus.STATUS_ERROR );
        _fileTransferRequest.setExecutionTime( Timestamp.from( Instant.now( ) ) );
        _fileTransferRequest.setCreationTime( Timestamp.from( Instant.now( ) ) );
        FileStorageTransferRequestHome.create( _fileTransferRequest );

        _error = new FileStorageTransferError( _fileTransferRequest.getId( ), 404, ERRORMESSAGE1, "trace", Timestamp.from( Instant.now( ) ) );
        FileStorageTransferErrorHome.create( _error );
    }

    /**
     * Removes the request and the error worked on
     *
     * @throws Exception
     *             if the parent tear down fails
     */
    @AfterEach
    @Override
    protected void tearDown( ) throws Exception
    {
        FileStorageTransferErrorHome.remove( _error.getId( ) );
        FileStorageTransferRequestHome.remove( _fileTransferRequest.getId( ) );

        super.tearDown( );
    }

    /**
     * Test of the views of the bean
     *
     * @throws Exception
     *             if the controller fails
     */
    @Test
    public void testViews( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), FileStorageTransferErrorJspBean.RIGHT_MANAGETRANSFERERROR );
        _bean.processController( request, new MockHttpServletResponse( ) );

        assertNotNull( _bean.getManageErrors( request, new ModelsImpl( ) ) );

        request.addParameter( "id", String.valueOf( _error.getId( ) ) );
        assertNotNull( _bean.getError( request, new ModelsImpl( ) ) );
    }
}
