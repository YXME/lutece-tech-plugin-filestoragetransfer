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

package fr.paris.lutece.plugins.filestoragetransfer.service.resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.service.FileStorageTransferFileInfoService;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 * Asks the resource link providers which business resource owns the file of each transfer request
 */
@ApplicationScoped
public class FileStorageTransferResourceService
{
    @Inject
    @Any
    private Instance<IFileStorageTransferResourceLinkProvider> _resourceLinkProviders;

    @Inject
    private FileStorageTransferFileInfoService _fileInfoService;

    /**
     * Returns the link to the business resource of each request whose file a provider knows
     *
     * @param listRequests
     *            the transfer requests
     * @param locale
     *            the locale of the labels
     * @return the links by request id; a request no provider knows is absent
     */
    public Map<Integer, FileStorageTransferResourceLink> getResourceLinks( List<FileStorageTransferRequest> listRequests, Locale locale )
    {
        Map<Integer, FileStorageTransferResourceLink> mapLinks = new HashMap<>( );
        Map<String, List<FileStorageTransferRequest>> mapRequestsByContext = listRequests.stream( )
                .collect( Collectors.groupingBy( request -> Objects.toString( request.getRequestContext( ), "" ) ) );

        for ( Map.Entry<String, List<FileStorageTransferRequest>> entry : mapRequestsByContext.entrySet( ) )
        {
            Map<String, FileStorageTransferResourceLink> mapLinksByKey = getResourceLinksByKey( entry.getKey( ),
                    entry.getValue( ).stream( ).map( _fileInfoService::getCurrentFileKey ).filter( Objects::nonNull ).collect( Collectors.toSet( ) ), locale );

            for ( FileStorageTransferRequest request : entry.getValue( ) )
            {
                FileStorageTransferResourceLink link = mapLinksByKey.get( _fileInfoService.getCurrentFileKey( request ) );

                if ( link != null )
                {
                    mapLinks.put( request.getId( ), link );
                }
            }
        }

        return mapLinks;
    }

    /**
     * Asks every provider of a context for the links of some file keys; the first provider that knows a key wins
     *
     * @param strRequestContext
     *            the context of the requests
     * @param listFileKeys
     *            the current keys of the files
     * @param locale
     *            the locale of the labels
     * @return the links by file key
     */
    private Map<String, FileStorageTransferResourceLink> getResourceLinksByKey( String strRequestContext, Collection<String> listFileKeys, Locale locale )
    {
        Map<String, FileStorageTransferResourceLink> mapLinksByKey = new HashMap<>( );

        if ( listFileKeys.isEmpty( ) )
        {
            return mapLinksByKey;
        }

        for ( IFileStorageTransferResourceLinkProvider provider : _resourceLinkProviders )
        {
            try
            {
                if ( provider.isInvoked( strRequestContext ) )
                {
                    provider.getResourceLinks( listFileKeys, locale ).forEach( mapLinksByKey::putIfAbsent );
                }
            }
            catch( RuntimeException e )
            {
                AppLogService.getLogger( ).warn( "Resource link provider {} failed for the context {}", provider.getClass( ).getName( ), strRequestContext, e );
            }
        }

        return mapLinksByKey;
    }
}
