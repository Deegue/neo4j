/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.internal.diagnostics;

import org.neo4j.logging.Log;

public enum DiagnosticsPhase
{
    REQUESTED( true, false ),
    EXPLICIT( true, false ),
    INITIALIZED( false, true ),
    STARTED( false, true ),
    STOPPING( false, false ),
    SHUTDOWN( false, false ), ;

    private final boolean requested;
    private final boolean initial;

    DiagnosticsPhase( boolean requested, boolean initial )
    {
        this.requested = requested;
        this.initial = initial;
    }

    void emitStart( Log log )
    {
        log.info( "--- " + this + " START ---" );
    }

    void emitDone( Log log )
    {
        log.info( "--- " + this + " END ---" );
    }

    void emitStart( Log log, DiagnosticsProvider provider )
    {
        log.info( "--- " + this + " for " + provider.getDiagnosticsIdentifier() + " START ---" );
    }

    void emitDone( Log log, DiagnosticsProvider provider )
    {
        log.info( "--- " + this + " for " + provider.getDiagnosticsIdentifier() + " END ---" );
    }

    public boolean isInitialization()
    {
        return initial;
    }

    public boolean isExplicitlyRequested()
    {
        return requested;
    }

    @Override
    public String toString()
    {
        return name() + " diagnostics";
    }
}
