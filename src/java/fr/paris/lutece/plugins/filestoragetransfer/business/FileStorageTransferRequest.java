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

import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * This is the business class for the object Request
 */
public class FileStorageTransferRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    @NotEmpty( message = "#i18n{filestoragetransfer.validation.request.SourceFileKey.notEmpty}" )
    @Size( max = 255, message = "#i18n{filestoragetransfer.validation.request.SourceFileKey.size}" )
    private String _strSourceFileKey;

    @NotEmpty( message = "#i18n{filestoragetransfer.validation.request.SourceFileserviceproviderName.notEmpty}" )
    @Size( max = 255, message = "#i18n{filestoragetransfer.validation.request.SourceFileserviceproviderName.size}" )
    private String _strSourceFileserviceproviderName;

    @NotEmpty( message = "#i18n{filestoragetransfer.validation.request.TargetFileserviceproviderName.notEmpty}" )
    @Size( max = 255, message = "#i18n{filestoragetransfer.validation.request.TargetFileserviceproviderName.size}" )
    private String _strTargetFileserviceproviderName;

    @Size( max = 255, message = "#i18n{filestoragetransfer.validation.request.SourceFileKey.size}" )
    private String _strTargetFileKey;

    private FileStorageTransferRequestStatus _nRequestStatus;

    private int _nRetryCount;

    private Timestamp _tExecutionTime;

    private Timestamp _tCreationTime;

    private String _strRequestContext;

    private String _strContactMail;

    // Constructor
    public FileStorageTransferRequest( String sourceFileKey, String sourceFileserviceproviderName, String targetFileserviceproviderName, String requestContext,
            String contactMail )
    {
        _strSourceFileKey = sourceFileKey;
        _strSourceFileserviceproviderName = sourceFileserviceproviderName;
        _strTargetFileserviceproviderName = targetFileserviceproviderName;
        _nRequestStatus = FileStorageTransferRequestStatus.STATUS_TODO;
        _nRetryCount = 0;
        _tExecutionTime = Timestamp.from( Instant.now( ) );
        _tCreationTime = Timestamp.from( Instant.now( ) );
        _strRequestContext = requestContext;
        _strContactMail = contactMail;
    }

    public FileStorageTransferRequest( )
    {

    }

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
     * Returns the SourceFileKey
     * 
     * @return The SourceFileKey
     */
    public String getSourceFileKey( )
    {
        return _strSourceFileKey;
    }

    /**
     * Sets the SourceFileKey
     * 
     * @param strSourceFileKey
     *            The SourceFileKey
     */
    public void setSourceFileKey( String strSourceFileKey )
    {
        _strSourceFileKey = strSourceFileKey;
    }

    /**
     * Returns the SourceFileserviceproviderName
     * 
     * @return The SourceFileserviceproviderName
     */
    public String getSourceFileserviceproviderName( )
    {
        return _strSourceFileserviceproviderName;
    }

    /**
     * Sets the SourceFileserviceproviderName
     * 
     * @param strSourceFileserviceproviderName
     *            The SourceFileserviceproviderName
     */
    public void setSourceFileserviceproviderName( String strSourceFileserviceproviderName )
    {
        _strSourceFileserviceproviderName = strSourceFileserviceproviderName;
    }

    /**
     * Returns the TargetFileKey
     * 
     * @return The TargetFileKey
     */
    public String getTargetFileKey( )
    {
        return _strTargetFileKey;
    }

    /**
     * Sets the TargetFileKey
     * 
     * @param strTargetFileKey
     *            The TargetFileKey
     */
    public void setTargetFileKey( String strTargetFileKey )
    {
        _strTargetFileKey = strTargetFileKey;
    }

    /**
     * Returns the TargetFileserviceproviderName
     * 
     * @return The TargetFileserviceproviderName
     */
    public String getTargetFileserviceproviderName( )
    {
        return _strTargetFileserviceproviderName;
    }

    /**
     * Sets the TargetFileserviceproviderName
     * 
     * @param strTargetFileserviceproviderName
     *            The TargetFileserviceproviderName
     */
    public void setTargetFileserviceproviderName( String strTargetFileserviceproviderName )
    {
        _strTargetFileserviceproviderName = strTargetFileserviceproviderName;
    }

    /**
     * Returns the RequestStatus
     * 
     * @return The RequestStatus
     */
    public FileStorageTransferRequestStatus getRequestStatus( )
    {
        return _nRequestStatus;
    }

    /**
     * Sets the RequestStatus
     * 
     * @param nRequestStatus
     *            The RequestStatus
     */
    public void setRequestStatus( FileStorageTransferRequestStatus nRequestStatus )
    {
        _nRequestStatus = nRequestStatus;
    }

    /**
     * Returns the RetryCount
     * 
     * @return The RetryCount
     */
    public int getRetryCount( )
    {
        return _nRetryCount;
    }

    /**
     * Sets the RetryCount
     * 
     * @param nRetryCount
     *            The RetryCount
     */
    public void setRetryCount( int nRetryCount )
    {
        _nRetryCount = nRetryCount;
    }

    public void upRetryCount( )
    {
        _nRetryCount = _nRetryCount + 1;
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

    /**
     * Get the CreationTime
     * 
     * @return The CreationTime
     */
    public Timestamp getCreationTime( )
    {
        return _tCreationTime;
    }

    /**
     * Set the CreationTime
     * 
     * @param tCreationTime
     *            The CreationTime
     */
    public void setCreationTime( Timestamp tCreationTime )
    {
        _tCreationTime = tCreationTime;
    }

    public String getRequestContext( )
    {
        return _strRequestContext;
    }

    public void setRequestContext( String strRequestContext )
    {
        _strRequestContext = strRequestContext;
    }

    public String getContactMail( )
    {
        return _strContactMail;
    }

    public void setContactMail( String strContactMail )
    {
        _strContactMail = strContactMail;
    }

}
