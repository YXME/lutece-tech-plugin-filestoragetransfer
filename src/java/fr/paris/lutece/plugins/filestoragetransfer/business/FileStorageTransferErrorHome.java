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

import java.util.List;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for Error objects
 */
public final class FileStorageTransferErrorHome
{
    // Static variable pointed at the DAO instance
    private static IFileStorageTransferErrorDAO _dao = CDI.current( ).select( IFileStorageTransferErrorDAO.class ).get( );
    private static Plugin _plugin = PluginService.getPlugin( "filestoragetransfer" );

    /**
     * Private constructor - this class need not be instantiated
     */
    FileStorageTransferErrorHome( )
    {
    }

    /**
     * Create an instance of the error class
     * 
     * @param error
     *            The instance of the Error which contains the informations to store
     * @return The instance of error which has been created with its primary key.
     */
    public static FileStorageTransferError create( FileStorageTransferError error )
    {
        _dao.insert( error, _plugin );

        return error;
    }

    /**
     * Update of the error which is specified in parameter
     * 
     * @param error
     *            The instance of the Error which contains the data to store
     * @return The instance of the error which has been updated
     */
    public static FileStorageTransferError update( FileStorageTransferError error )
    {
        _dao.store( error, _plugin );

        return error;
    }

    /**
     * Remove the error whose identifier is specified in parameter
     * 
     * @param nKey
     *            The error Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a error whose identifier is specified in parameter
     * 
     * @param nKey
     *            The error primary key
     * @return an instance of Error
     */
    public static Optional<FileStorageTransferError> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the error objects and returns them as a list
     * 
     * @return the list which contains the data of all the error objects
     */
    public static List<FileStorageTransferError> getErrorsList( )
    {
        return _dao.selectErrorsList( _plugin );
    }

    /**
     * Load the id of all the error objects and returns them as a list
     * 
     * @return the list which contains the id of all the error objects
     */
    public static List<Integer> getIdErrorsList( )
    {
        return _dao.selectIdErrorsList( _plugin );
    }

    /**
     * Load the data of all the error objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the error objects
     */
    public static ReferenceList getErrorsReferenceList( )
    {
        return _dao.selectErrorsReferenceList( _plugin );
    }

    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param listIds
     *            liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<FileStorageTransferError> getErrorsListByIds( List<Integer> listIds )
    {
        return _dao.selectErrorsListByIds( _plugin, listIds );
    }

    /**
     * Load the data of all errors with a specific request id
     * 
     * @return the list which contains the data of all errors with a specific request id
     */
    public static List<FileStorageTransferError> getErrorsListByRequestId( int RequestId )
    {
        return _dao.selectErrorsListByRequestId( _plugin, RequestId );
    }

}
