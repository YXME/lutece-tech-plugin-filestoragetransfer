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
package fr.paris.lutece.plugins.filestoragetransfer.daemon;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequestHome;
import fr.paris.lutece.plugins.filestoragetransfer.service.FileSwitcherService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class TransferFileDaemon extends Daemon
{

    private static int _nBatchSize;

    public TransferFileDaemon(  )
    {
        _nBatchSize = AppPropertiesService.getPropertyInt( "filestoragetransfer.UploadLimit", 0 );
    }

    public void run( )
    {
        Timestamp executionTime = Timestamp.from(Instant.now());

        List<FileTransferRequest> listRequest = FileTransferRequestHome.selectRequestsListByStatusAndExecutionTime (  executionTime, _nBatchSize );
        
        listRequest.forEach( request -> {
            try
            {
                FileSwitcherService.TransferFileToNewFileService( request );
                this.appendLastRunLogs( "Request " + request.getId() + ": DONE.\n"  );
            }
            catch( Exception e ) 
            {
                this.appendLastRunLogs( "Request " + request.getId() + " : FAILED ->" + e.getMessage() + "\n" );
            }
        });
    }
}