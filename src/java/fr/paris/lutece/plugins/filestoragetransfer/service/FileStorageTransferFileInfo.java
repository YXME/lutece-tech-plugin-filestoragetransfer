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

package fr.paris.lutece.plugins.filestoragetransfer.service;

import java.io.Serializable;

/**
 * What the back office shows of the file of a transfer request, read from its current file store provider
 */
public class FileStorageTransferFileInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String _strFileKey;
    private final String _strFileserviceproviderName;
    private String _strName;
    private long _lSize;
    private String _strMimeType;
    private String _strDownloadUrl;
    private boolean _bReadable;

    /**
     * Constructor
     *
     * @param strFileKey
     *            the current key of the file
     * @param strFileserviceproviderName
     *            the name of the file store provider that currently holds the file
     */
    public FileStorageTransferFileInfo( String strFileKey, String strFileserviceproviderName )
    {
        _strFileKey = strFileKey;
        _strFileserviceproviderName = strFileserviceproviderName;
    }

    /**
     * Returns the current key of the file
     *
     * @return the key
     */
    public String getFileKey( )
    {
        return _strFileKey;
    }

    /**
     * Returns the name of the file store provider that currently holds the file
     *
     * @return the provider name
     */
    public String getFileserviceproviderName( )
    {
        return _strFileserviceproviderName;
    }

    /**
     * Returns the name of the file
     *
     * @return the name, null when the file could not be read
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the name of the file
     *
     * @param strName
     *            the name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Returns the size of the file in bytes
     *
     * @return the size
     */
    public long getSize( )
    {
        return _lSize;
    }

    /**
     * Sets the size of the file in bytes
     *
     * @param lSize
     *            the size
     */
    public void setSize( long lSize )
    {
        _lSize = lSize;
    }

    /**
     * Returns the MIME type of the file
     *
     * @return the MIME type
     */
    public String getMimeType( )
    {
        return _strMimeType;
    }

    /**
     * Sets the MIME type of the file
     *
     * @param strMimeType
     *            the MIME type
     */
    public void setMimeType( String strMimeType )
    {
        _strMimeType = strMimeType;
    }

    /**
     * Returns the back office download URL of the file
     *
     * @return the URL, null when the file could not be read
     */
    public String getDownloadUrl( )
    {
        return _strDownloadUrl;
    }

    /**
     * Sets the back office download URL of the file
     *
     * @param strDownloadUrl
     *            the URL
     */
    public void setDownloadUrl( String strDownloadUrl )
    {
        _strDownloadUrl = strDownloadUrl;
    }

    /**
     * Tells whether the provider could read the file
     *
     * @return true when the metadata of the file were read
     */
    public boolean isReadable( )
    {
        return _bReadable;
    }

    /**
     * Sets whether the provider could read the file
     *
     * @param bReadable
     *            true when the metadata of the file were read
     */
    public void setReadable( boolean bReadable )
    {
        _bReadable = bReadable;
    }
}
