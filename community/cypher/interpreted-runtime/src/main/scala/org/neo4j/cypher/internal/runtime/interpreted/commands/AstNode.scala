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
package org.neo4j.cypher.internal.runtime.interpreted.commands

import org.neo4j.cypher.internal.v3_5.util.CypherTypeException
import org.neo4j.cypher.internal.runtime.interpreted.commands.expressions.Expression

import scala.reflect.ClassTag


trait AstNode[T] {
  def children: Seq[AstNode[_]]

  def rewrite(f: Expression => Expression): T

  def typedRewrite[R <: T](f: Expression => Expression)(implicit mf: ClassTag[R]): R = rewrite(f) match {
    case (value: R) => value
    case _          => throw new CypherTypeException("Invalid rewrite")
  }

  def contains(e:Expression) = exists(e == _)

  def exists(f: Expression => Boolean) = filter(f).nonEmpty

  def filter(isMatch: Expression => Boolean): Seq[Expression] =
  // We use our visit method to create an traversable, from which we create the Seq
    new Traversable[Expression] {
      def foreach[U](f: Expression => U) {
        visit {
          case e: Expression if isMatch(e) => f(e)
        }
      }
    }.toIndexedSeq

  def visitChildren(f: PartialFunction[AstNode[_], Any]) {
    children.foreach(child => {
      if (f.isDefinedAt(child)) {
        f(child)
      }

      child.visitChildren(f)
    })
  }

  def visit(f: PartialFunction[AstNode[_], Any]) {
    visitChildren(f)

    if (f.isDefinedAt(this)) {
      f(this)
    }
  }


  def visitFirst(f: PartialFunction[AstNode[_], Any]) {
    if (f.isDefinedAt(this)) {
      f(this)
    }

    visitChildren(f)
  }
}

