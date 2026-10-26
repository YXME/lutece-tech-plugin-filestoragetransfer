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

package fr.paris.lutece.plugins.filestoragetransfer.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import jakarta.enterprise.inject.spi.CDI;
import fr.paris.lutece.util.ReferenceList;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for Request objects
 */
public final class FileStorageTransferRequestHome
{
    // Static variable pointed at the DAO instance
    private static IFileStorageTransferRequestDAO _dao = CDI.current( ).select( IFileStorageTransferRequestDAO.class ).get( );
    private static Plugin _plugin = PluginService.getPlugin( "filestoragetransfer" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FileStorageTransferRequestHome( )
    {
    }

    /**
     * Create an instance of the request class
     * 
     * @param request
     *            The instance of the Request which contains the informations to store
     * @return The instance of request which has been created with its primary key.
     */
    public static FileStorageTransferRequest create( FileStorageTransferRequest request )
    {
        _dao.insert( request, _plugin );

        return request;
    }

    /**
     * Update of the request which is specified in parameter
     * 
     * @param request
     *            The instance of the Request which contains the data to store
     * @return The instance of the request which has been updated
     */
    public static FileStorageTransferRequest update( FileStorageTransferRequest request )
    {
        _dao.store( request, _plugin );

        return request;
    }

    /**
     * Remove the request whose identifier is specified in parameter
     * 
     * @param nKey
     *            The request Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a request whose identifier is specified in parameter
     * 
     * @param nKey
     *            The request primary key
     * @return an instance of Request
     */
    public static Optional<FileStorageTransferRequest> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the request objects and returns them as a list
     * 
     * @return the list which contains the data of all the request objects
     */
    public static List<FileStorageTransferRequest> getRequestsList( )
    {
        return _dao.selectRequestsList( _plugin );
    }

    /**
     * Load the id of all the request objects and returns them as a list
     * 
     * @return the list which contains the id of all the request objects
     */
    public static List<Integer> getIdRequestsList( )
    {
        return _dao.selectIdRequestsList( _plugin );
    }

    /**
     * Load the id of all the request objects with a specific status and returns them as a list
     * 
     * @return the list which contains the id of all the request objects
     */
    public static List<Integer> getIdRequestsListByStatus( String status )
    {
        return _dao.selectIdRequestsListByStatus( _plugin, status );
    }

    /**
     * Load the data of all the request objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the request objects
     */
    public static ReferenceList getRequestsReferenceList( )
    {
        return _dao.selectRequestsReferenceList( _plugin );
    }

    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param listIds
     *            liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<FileStorageTransferRequest> getRequestsListByIds( List<Integer> listIds )
    {
        return _dao.selectRequestsListByIds( _plugin, listIds );
    }

    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @param RequestStatus
     *            Status of the request
     * @return The list which contains the data of all the avant objects
     */
    public static List<FileStorageTransferRequest> getRequestsListByStatus( String status )
    {
        return _dao.selectRequestsListByStatus( _plugin, status );
    }

    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @param RequestStatus
     *            Status of the request
     * @return The list which contains the data of all the avant objects
     */
    public static List<FileStorageTransferRequest> selectRequestsListByStatusAndExecutionTime( Timestamp executionTime, int limit )
    {
        return _dao.selectRequestsListToExecute( _plugin, executionTime, limit );
    }

    /**
     * Counts the requests of each status
     *
     * @return the number of requests by status value; a status without request is absent
     */
    public static Map<String, Integer> countByStatus( )
    {
        return _dao.selectCountByStatus( _plugin );
    }

}
