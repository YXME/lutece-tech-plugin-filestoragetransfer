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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
 * Test of the request JspBean
 */
public class FileTransferRequestJspBeanTest extends LuteceTestCase
{
    private static final String SOURCEFILEKEY1 = "SourceFileKey1";
    private static final String SOURCEFILEKEY2 = "SourceFileKey2";
    private static final String SOURCEFILESERVICEPROVIDERNAME1 = "SourceFileserviceproviderName1";
    private static final String SOURCEFILESERVICEPROVIDERNAME2 = "SourceFileserviceproviderName2";
    private static final String TARGETFILESERVICEPROVIDERNAME1 = "TargetFileserviceproviderName1";
    private static final String TARGETFILESERVICEPROVIDERNAME2 = "TargetFileserviceproviderName2";

    @Inject
    private FileStorageTransferRequestJspBean _bean;

    private FileStorageTransferRequest _fileTransferRequest;

    /**
     * Creates a request to work on
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
        _fileTransferRequest.setSourceFileKey( SOURCEFILEKEY1 );
        _fileTransferRequest.setSourceFileserviceproviderName( SOURCEFILESERVICEPROVIDERNAME1 );
        _fileTransferRequest.setTargetFileserviceproviderName( TARGETFILESERVICEPROVIDERNAME1 );
        _fileTransferRequest.setRequestStatus( FileStorageTransferRequestStatus.STATUS_TODO );
        _fileTransferRequest.setExecutionTime( Timestamp.from( Instant.now( ) ) );
        _fileTransferRequest.setCreationTime( Timestamp.from( Instant.now( ) ) );
        FileStorageTransferRequestHome.create( _fileTransferRequest );
    }

    /**
     * Removes the request worked on
     *
     * @throws Exception
     *             if the parent tear down fails
     */
    @AfterEach
    @Override
    protected void tearDown( ) throws Exception
    {
        FileStorageTransferRequestHome.remove( _fileTransferRequest.getId( ) );

        super.tearDown( );
    }

    /**
     * Builds a request of an administrator holding the right of the feature
     *
     * @return the request
     */
    private MockHttpServletRequest getAdminRequest( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), FileStorageTransferRequestJspBean.RIGHT_MANAGETRANSFERREQUEST );

        return request;
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
        MockHttpServletRequest request = getAdminRequest( );
        _bean.processController( request, new MockHttpServletResponse( ) );

        assertNotNull( _bean.getManageRequests( request, new ModelsImpl( ) ) );

        for ( String strStatus : new String [ ] {
                "TODO", "DONE", "FAILED", "ERROR", "UNKNOWN", "not-a-status"
        } )
        {
            MockHttpServletRequest requestTab = getAdminRequest( );
            requestTab.addParameter( "status", strStatus );
            assertNotNull( _bean.getManageRequests( requestTab, new ModelsImpl( ) ) );
        }
        String strCreate = _bean.getCreateRequest( request, new ModelsImpl( ) );
        assertTrue( strCreate.contains( "value=\"defaultDatabaseFileStoreProvider\"" ), "technical name posted" );
        assertTrue( strCreate.contains( "Base de donn" ) || strCreate.contains( "Lutece database" ), "label shown" );

        request.addParameter( "id", String.valueOf( _fileTransferRequest.getId( ) ) );
        assertNotNull( _bean.getViewRequest( request, new ModelsImpl( ) ) );
        assertNotNull( _bean.getModifyRequest( request, new ModelsImpl( ) ) );
    }

    /**
     * Test of the creation action
     *
     * @throws Exception
     *             if the controller fails
     */
    @Test
    public void testCreateRequest( ) throws Exception
    {
        List<Integer> listIdsBefore = FileStorageTransferRequestHome.getIdRequestsList( );

        MockHttpServletRequest request = getAdminRequest( );
        request.addParameter( "action", "createRequest" );
        request.addParameter( "source_file_key", SOURCEFILEKEY2 );
        request.addParameter( "source_fileserviceprovider_name", SOURCEFILESERVICEPROVIDERNAME2 );
        request.addParameter( "target_fileserviceprovider_name", TARGETFILESERVICEPROVIDERNAME2 );
        request.setMethod( "POST" );
        _bean.processController( request, new MockHttpServletResponse( ) );

        List<Integer> listIdsAfter = FileStorageTransferRequestHome.getIdRequestsList( );
        listIdsAfter.removeAll( listIdsBefore );
        assertEquals( 1, listIdsAfter.size( ), "one request created" );

        FileStorageTransferRequest created = FileStorageTransferRequestHome.findByPrimaryKey( listIdsAfter.get( 0 ) ).orElseThrow( );
        assertEquals( SOURCEFILEKEY2, created.getSourceFileKey( ), "source file key bound" );
        assertEquals( FileStorageTransferRequestStatus.STATUS_TODO, created.getRequestStatus( ), "new request to do" );

        FileStorageTransferRequestHome.remove( created.getId( ) );
    }

    /**
     * Test of the modification action
     *
     * @throws Exception
     *             if the controller fails
     */
    @Test
    public void testModifyRequest( ) throws Exception
    {
        MockHttpServletRequest request = getAdminRequest( );
        request.addParameter( "action", "modifyRequest" );
        request.addParameter( "id", String.valueOf( _fileTransferRequest.getId( ) ) );
        request.addParameter( "source_file_key", SOURCEFILEKEY2 );
        request.addParameter( "source_fileserviceprovider_name", SOURCEFILESERVICEPROVIDERNAME2 );
        request.addParameter( "target_fileserviceprovider_name", TARGETFILESERVICEPROVIDERNAME2 );
        request.setMethod( "POST" );
        _bean.processController( request, new MockHttpServletResponse( ) );

        FileStorageTransferRequest modified = FileStorageTransferRequestHome.findByPrimaryKey( _fileTransferRequest.getId( ) ).orElseThrow( );
        assertEquals( SOURCEFILEKEY2, modified.getSourceFileKey( ), "source file key updated" );
        assertEquals( TARGETFILESERVICEPROVIDERNAME2, modified.getTargetFileserviceproviderName( ), "target updated" );
    }

    /**
     * A modification saved after the daemon changed the request keeps what the daemon wrote
     *
     * @throws Exception
     *             if the controller fails
     */
    @Test
    public void testModifyKeepsDaemonChanges( ) throws Exception
    {
        MockHttpServletRequest requestView = getAdminRequest( );
        requestView.addParameter( "id", String.valueOf( _fileTransferRequest.getId( ) ) );
        assertNotNull( _bean.getModifyRequest( requestView, new ModelsImpl( ) ) );

        FileStorageTransferRequest transferred = FileStorageTransferRequestHome.findByPrimaryKey( _fileTransferRequest.getId( ) ).orElseThrow( );
        transferred.setRequestStatus( FileStorageTransferRequestStatus.STATUS_DONE );
        transferred.setTargetFileKey( "daemon-target-key" );
        FileStorageTransferRequestHome.update( transferred );

        MockHttpServletRequest request = getAdminRequest( );
        request.addParameter( "action", "modifyRequest" );
        request.addParameter( "id", String.valueOf( _fileTransferRequest.getId( ) ) );
        request.addParameter( "source_file_key", SOURCEFILEKEY2 );
        request.addParameter( "source_fileserviceprovider_name", SOURCEFILESERVICEPROVIDERNAME2 );
        request.addParameter( "target_fileserviceprovider_name", TARGETFILESERVICEPROVIDERNAME2 );
        request.setMethod( "POST" );
        _bean.processController( request, new MockHttpServletResponse( ) );

        FileStorageTransferRequest stored = FileStorageTransferRequestHome.findByPrimaryKey( _fileTransferRequest.getId( ) ).orElseThrow( );
        assertEquals( SOURCEFILEKEY2, stored.getSourceFileKey( ), "modification saved" );
        assertEquals( FileStorageTransferRequestStatus.STATUS_DONE, stored.getRequestStatus( ), "status written by the daemon kept" );
        assertEquals( "daemon-target-key", stored.getTargetFileKey( ), "target key written by the daemon kept" );
    }

    /**
     * Test of the removal action
     *
     * @throws Exception
     *             if the controller fails
     */
    @Test
    public void testRemoveRequest( ) throws Exception
    {
        MockHttpServletRequest request = getAdminRequest( );
        request.addParameter( "action", "removeRequest" );
        request.addParameter( "id", String.valueOf( _fileTransferRequest.getId( ) ) );
        request.setMethod( "POST" );
        _bean.processController( request, new MockHttpServletResponse( ) );

        Optional<FileStorageTransferRequest> removed = FileStorageTransferRequestHome.findByPrimaryKey( _fileTransferRequest.getId( ) );
        assertFalse( removed.isPresent( ), "request removed" );
    }

    /**
     * Test of the reset of every request in error
     *
     * @throws Exception
     *             if the controller fails
     */
    @Test
    public void testResetAllErrorRequests( ) throws Exception
    {
        _fileTransferRequest.setRequestStatus( FileStorageTransferRequestStatus.STATUS_ERROR );
        FileStorageTransferRequestHome.update( _fileTransferRequest );

        MockHttpServletRequest request = getAdminRequest( );
        request.addParameter( "action", "resetStatusOfAllErrorRequests" );
        request.setMethod( "POST" );
        _bean.processController( request, new MockHttpServletResponse( ) );

        FileStorageTransferRequest reset = FileStorageTransferRequestHome.findByPrimaryKey( _fileTransferRequest.getId( ) ).orElseThrow( );
        assertTrue( FileStorageTransferRequestStatus.STATUS_TODO.equals( reset.getRequestStatus( ) ), "request in error reset to do" );
    }
}
