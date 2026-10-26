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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequestStatus;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Test of the resource link service
 */
public class FileStorageTransferResourceServiceTest extends LuteceTestCase
{
    @Inject
    private FileStorageTransferResourceService _resourceService;

    /**
     * Builds a request
     *
     * @param nId
     *            the id
     * @param strContext
     *            the context
     * @param strSourceKey
     *            the source file key
     * @return the request
     */
    private static FileStorageTransferRequest newRequest( int nId, String strContext, String strSourceKey )
    {
        FileStorageTransferRequest request = new FileStorageTransferRequest( );
        request.setId( nId );
        request.setRequestContext( strContext );
        request.setSourceFileKey( strSourceKey );
        request.setRequestStatus( FileStorageTransferRequestStatus.STATUS_TODO );

        return request;
    }

    /**
     * Only the requests whose context and key a provider knows get a link, and a failing provider breaks nothing
     */
    @Test
    public void testResourceLinks( )
    {
        FileStorageTransferRequest known = newRequest( 1, TestResourceLinkProvider.CONTEXT, TestResourceLinkProvider.KNOWN_KEY );
        FileStorageTransferRequest unknownKey = newRequest( 2, TestResourceLinkProvider.CONTEXT, "other-key" );
        FileStorageTransferRequest otherContext = newRequest( 3, "no-provider-context", TestResourceLinkProvider.KNOWN_KEY );
        FileStorageTransferRequest noContext = newRequest( 4, null, TestResourceLinkProvider.KNOWN_KEY );
        FileStorageTransferRequest failing = newRequest( 5, TestResourceLinkProvider.FAILING_CONTEXT, "fail" );

        Map<Integer, FileStorageTransferResourceLink> mapLinks = _resourceService.getResourceLinks( List.of( known, unknownKey, otherContext, noContext, failing ),
                Locale.FRENCH );

        assertTrue( mapLinks.containsKey( 1 ), "known key linked" );
        assertEquals( TestResourceLinkProvider.URL, mapLinks.get( 1 ).getUrl( ), "link url" );
        assertFalse( mapLinks.containsKey( 2 ), "unknown key not linked" );
        assertFalse( mapLinks.containsKey( 3 ), "context without provider not linked" );
        assertFalse( mapLinks.containsKey( 4 ), "no context not linked" );
        assertFalse( mapLinks.containsKey( 5 ), "failing provider ignored" );
    }
}
