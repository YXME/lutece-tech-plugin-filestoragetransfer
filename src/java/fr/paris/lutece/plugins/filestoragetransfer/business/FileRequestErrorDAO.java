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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides Data Access methods for Error objects
 */
public final class FileRequestErrorDAO implements IFileRequestErrorDAO
{
    // Constants
    private static final String SQL_QUERY_INSERT = "INSERT INTO filestoragetransfer_error ( id_request, code, error_message, error_trace, execution_time ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM filestoragetransfer_error WHERE id_error = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE filestoragetransfer_error SET id_request = ?, code = ?, error_message = ?,  error_trace = ?, execution_time = ? WHERE id_error = ?";

    private static final String SQL_QUERY_SELECTALL = "SELECT id_error, id_request, code, error_message, error_trace, execution_time FROM filestoragetransfer_error";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_error FROM filestoragetransfer_error";

    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECTALL + " WHERE id_error IN (  ";
    private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + " WHERE id_error = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_REQUEST = SQL_QUERY_SELECTALL + " WHERE id_request = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FileRequestError error, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, error.getIdRequest( ) );
            daoUtil.setInt( nIndex++, error.getCode( ) );
            daoUtil.setString( nIndex++, error.getErrorMessage( ) );
            daoUtil.setString( nIndex++, error.getErrorTrace( ) );
            daoUtil.setTimestamp( nIndex++, error.getExecutionTime( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                error.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<FileRequestError> load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            FileRequestError error = null;

            if ( daoUtil.next( ) )
            {
                error = loadFromDaoUtil( daoUtil );
            }

            return Optional.ofNullable( error );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( FileRequestError error, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, error.getIdRequest( ) );
            daoUtil.setInt( nIndex++, error.getCode( ) );
            daoUtil.setString( nIndex++, error.getErrorMessage( ) );
            daoUtil.setString( nIndex++, error.getErrorTrace( ) );
            daoUtil.setInt( nIndex, error.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FileRequestError> selectErrorsList( Plugin plugin )
    {
        List<FileRequestError> errorList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                errorList.add( loadFromDaoUtil( daoUtil ) );
            }

            return errorList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdErrorsList( Plugin plugin )
    {
        List<Integer> errorList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                errorList.add( daoUtil.getInt( 1 ) );
            }

            return errorList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectErrorsReferenceList( Plugin plugin )
    {
        ReferenceList errorList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                errorList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return errorList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FileRequestError> selectErrorsListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<FileRequestError> errorList = new ArrayList<>( );

        StringBuilder builder = new StringBuilder( );

        if ( !listIds.isEmpty( ) )
        {
            for ( int i = 0; i < listIds.size( ); i++ )
            {
                builder.append( "?," );
            }

            String placeHolders = builder.deleteCharAt( builder.length( ) - 1 ).toString( );
            String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";

            try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
            {
                int index = 1;
                for ( Integer n : listIds )
                {
                    daoUtil.setInt( index++, n );
                }

                daoUtil.executeQuery( );
                while ( daoUtil.next( ) )
                {
                    errorList.add( loadFromDaoUtil( daoUtil ) );
                }

                daoUtil.free( );

            }
        }
        return errorList;

    }


    /**
     * {@inheritDoc }
     */
    @Override
    public List<FileRequestError> selectErrorsListByRequestId( Plugin plugin, int RequestId )
    {
        List<FileRequestError> errorList = new ArrayList<>( );
        
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_REQUEST, plugin ) )
        {
            daoUtil.setInt( 1, RequestId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                errorList.add( loadFromDaoUtil( daoUtil ) );
            }

        }

        return errorList;
    }

    private FileRequestError loadFromDaoUtil( DAOUtil daoUtil )
    {

        FileRequestError error = new FileRequestError( );
        int nIndex = 1;

        error.setId( daoUtil.getInt( nIndex++ ) );
        error.setIdRequest( daoUtil.getInt( nIndex++ ) );
        error.setCode( daoUtil.getInt( nIndex++) );
        error.setErrorMessage( daoUtil.getString( nIndex++ ) );
        error.setErrorTrace( daoUtil.getString( nIndex++ ) );
        error.setExecutionTime( daoUtil.getTimestamp( nIndex ) );

        return error;
    }
}
