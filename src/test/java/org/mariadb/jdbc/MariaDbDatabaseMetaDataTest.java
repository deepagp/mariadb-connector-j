/*
 *
 * MariaDB Client for Java
 *
 * Copyright (c) 2012-2014 Monty Program Ab.
 * Copyright (c) 2015-2019 MariaDB Ab.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to Monty Program Ab info@montyprogram.com.
 *
 * This particular MariaDB Client for Java file is work
 * derived from a Drizzle-JDBC. Drizzle-JDBC file which is covered by subject to
 * the following copyright and notice provisions:
 *
 * Copyright (c) 2009-2011, Marcus Eriksson
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of the driver nor the names of its contributors may not be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS  AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package org.mariadb.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Test;

public class MariaDbDatabaseMetaDataTest extends BaseTest {

  /**
   * CONJ-412: tinyInt1isBit and yearIsDateType is not applied in method columnTypeClause.
   *
   * @throws Exception if exception occur
   */
  @Test
  public void testYearDataType() throws Exception {
    createTable(
        "yearTableMeta", "xx tinyint(1), x2 tinyint(1) unsigned, yy year(4), zz bit, uu smallint");
    try (Connection connection = setConnection()) {
      checkResults(connection, true, true);
    }

    try (Connection connection = setConnection("&yearIsDateType=false&tinyInt1isBit=false")) {
      checkResults(connection, false, false);
    }
  }

  private void checkResults(Connection connection, boolean yearAsDate, boolean tinyAsBit)
      throws SQLException {
    DatabaseMetaData meta = connection.getMetaData();
    ResultSet rs = meta.getColumns(null, null, "yearTableMeta", null);
    assertTrue(rs.next());
    assertEquals(tinyAsBit ? "BIT" : "TINYINT", rs.getString(6));
    assertTrue(rs.next());
    assertEquals(tinyAsBit ? "BIT" : "TINYINT UNSIGNED", rs.getString(6));
    assertTrue(rs.next());
    assertEquals(yearAsDate ? "YEAR" : "SMALLINT", rs.getString(6));
    assertEquals(yearAsDate ? null : "5", rs.getString(7)); // column size
    assertEquals(yearAsDate ? null : "0", rs.getString(9)); // decimal digit
  }
}
