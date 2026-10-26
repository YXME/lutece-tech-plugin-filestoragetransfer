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
 *"
 * License 1.0
 */

package fr.paris.lutece.plugins.filestoragetransfer.business;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferError;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferErrorHome;
import fr.paris.lutece.test.LuteceTestCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

/**
 * This is the business class test for the object Error
 */
public class ErrorBusinessTest extends LuteceTestCase
{
    private static final String ERRORMESSAGE1 = "ErrorMessage1";
    private static final String ERRORMESSAGE2 = "ErrorMessage2";

    /**
     * test Error
     */
    @Test
    public void testBusiness( )
    {
        FileStorageTransferRequest request1 = createRequest( );
        FileStorageTransferRequest request2 = createRequest( );

        // Initialize an object
        FileStorageTransferError error = new FileStorageTransferError( request1.getId( ), 404, ERRORMESSAGE1, "trace", Timestamp.from( Instant.now( ) ) );

        // Create test
        FileStorageTransferErrorHome.create( error );
        Optional<FileStorageTransferError> optErrorStored = FileStorageTransferErrorHome.findByPrimaryKey( error.getId( ) );
        FileStorageTransferError errorStored = optErrorStored.orElse( new FileStorageTransferError( ) );
        assertEquals( errorStored.getIdRequest( ), error.getIdRequest( ) );
        assertEquals( errorStored.getErrorMessage( ), error.getErrorMessage( ) );

        // Update test
        error.setIdRequest( request2.getId( ) );
        error.setErrorMessage( ERRORMESSAGE2 );
        FileStorageTransferErrorHome.update( error );
        optErrorStored = FileStorageTransferErrorHome.findByPrimaryKey( error.getId( ) );
        errorStored = optErrorStored.orElse( new FileStorageTransferError( ) );

        assertEquals( errorStored.getIdRequest( ), error.getIdRequest( ) );
        assertEquals( errorStored.getErrorMessage( ), error.getErrorMessage( ) );

        // List test
        FileStorageTransferErrorHome.getErrorsList( );

        // Delete test
        FileStorageTransferErrorHome.remove( error.getId( ) );
        optErrorStored = FileStorageTransferErrorHome.findByPrimaryKey( error.getId( ) );
        errorStored = optErrorStored.orElse( null );
        assertNull( errorStored );

        FileStorageTransferRequestHome.remove( request1.getId( ) );
        FileStorageTransferRequestHome.remove( request2.getId( ) );
    }

    /**
     * Creates the request an error belongs to
     *
     * @return the request
     */
    private FileStorageTransferRequest createRequest( )
    {
        Timestamp now = Timestamp.from( Instant.now( ) );
        FileStorageTransferRequest request = new FileStorageTransferRequest( );
        request.setSourceFileKey( "SourceFileKey" );
        request.setSourceFileserviceproviderName( "SourceFileserviceproviderName" );
        request.setTargetFileserviceproviderName( "TargetFileserviceproviderName" );
        request.setRequestStatus( FileStorageTransferRequestStatus.STATUS_ERROR );
        request.setExecutionTime( now );
        request.setCreationTime( now );

        return FileStorageTransferRequestHome.create( request );
    }

}
