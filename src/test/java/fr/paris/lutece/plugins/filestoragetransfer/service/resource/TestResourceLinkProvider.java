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

package fr.paris.lutece.plugins.filestoragetransfer.service.resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Resource link provider of the tests: it knows the key {@link #KNOWN_KEY} of the context {@link #CONTEXT}, and fails for the context
 * {@link #FAILING_CONTEXT}
 */
@ApplicationScoped
public class TestResourceLinkProvider implements IFileStorageTransferResourceLinkProvider
{
    public static final String CONTEXT = "unit-test-context";
    public static final String FAILING_CONTEXT = "unit-test-failing-context";
    public static final String KNOWN_KEY = "unit-test-known-key";
    public static final String URL = "jsp/admin/plugins/test/ManageResources.jsp?id=1";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvoked( String strRequestContext )
    {
        return CONTEXT.equals( strRequestContext ) || FAILING_CONTEXT.equals( strRequestContext );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, FileStorageTransferResourceLink> getResourceLinks( Collection<String> listFileKeys, Locale locale )
    {
        if ( listFileKeys.contains( "fail" ) )
        {
            throw new IllegalStateException( "provider failure of the test" );
        }

        Map<String, FileStorageTransferResourceLink> mapLinks = new HashMap<>( );

        if ( listFileKeys.contains( KNOWN_KEY ) )
        {
            mapLinks.put( KNOWN_KEY, new FileStorageTransferResourceLink( URL, "Test resource" ) );
        }

        return mapLinks;
    }
}
