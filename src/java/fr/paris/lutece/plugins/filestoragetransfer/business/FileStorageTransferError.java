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

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * This is the business class for the object Error
 */
public class FileStorageTransferError implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    private int _nIdRequest;

    private int _nCode;

    @NotEmpty( message = "#i18n{filestoragetransfer.validation.error.ErrorMessage.notEmpty}" )
    private String _strErrorMessage;

    private String _strErrorTrace;

    private Timestamp _tExecutionTime;

    public FileStorageTransferError( )
    {
    }

    public FileStorageTransferError( int nIdRequest, int nCode, String strErrorMessage, String strErrorTrace, Timestamp tExecutionTime )
    {
        _nIdRequest = nIdRequest;
        _nCode = nCode;
        _strErrorMessage = strErrorMessage;
        _strErrorTrace = strErrorTrace;
        _tExecutionTime = tExecutionTime;
    }

    // Heure
    // Nombre de rejeu (applicatif ou dans un historique ?)

    /**
     * Returns the Id
     * 
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * 
     * @param nId
     *            The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the IdRequest
     * 
     * @return The IdRequest
     */
    public int getIdRequest( )
    {
        return _nIdRequest;
    }

    /**
     * Sets the IdRequest
     * 
     * @param nIdRequest
     *            The IdRequest
     */
    public void setIdRequest( int nIdRequest )
    {
        _nIdRequest = nIdRequest;
    }

    /**
     * Returns the Code
     * 
     * @return The Code
     */
    public int getCode( )
    {
        return _nCode;
    }

    /**
     * Sets the Code
     * 
     * @param nCode
     *            The Code
     */
    public void setCode( int nCode )
    {
        _nCode = nCode;
    }

    /**
     * Returns the ErrorMessage
     * 
     * @return The ErrorMessage
     */
    public String getErrorMessage( )
    {
        return _strErrorMessage;
    }

    /**
     * Sets the ErrorMessage
     * 
     * @param strErrorMessage
     *            The ErrorMessage
     */
    public void setErrorMessage( String strErrorMessage )
    {
        _strErrorMessage = strErrorMessage;
    }

    /**
     * Returns the ErrorTrace
     * 
     * @return the ErrorTrace
     */
    public String getErrorTrace( )
    {
        return _strErrorTrace;
    }

    /**
     * Sets the ErrorTrace
     * 
     * @param strErrorTrace
     *            The ErrorTrace
     */
    public void setErrorTrace( String strErrorTrace )
    {
        _strErrorTrace = strErrorTrace;
    }

    /**
     * Get the ExecutionTime
     * 
     * @return The ExecutionTime
     */
    public Timestamp getExecutionTime( )
    {
        return _tExecutionTime;
    }

    /**
     * Set the ExecutionTime
     * 
     * @param tExecutionTime
     *            The ExecutionTime
     */
    public void setExecutionTime( Timestamp tExecutionTime )
    {
        _tExecutionTime = tExecutionTime;
    }

}
