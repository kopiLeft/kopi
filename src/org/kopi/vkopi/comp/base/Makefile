##
## Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
##
## This program is free software; you can redistribute it and/or modify
## it under the terms of the GNU General Public License version 2
## as published by the Free Software Foundation.
##
## This program is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
## GNU General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with this program; if not, write to the Free Software
## Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
##
## $Id$
##

TOPDIR =	../../..

PACKAGE =	org/kopi/vkopi/comp/base

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

GRM1VOC =	Base
GRM1DEP =
SCANNER1 =	Base

OPTIONS =	VKOptions

# Compiler classes
JAVAFILES +=	BaseScanner Parser VKParseContext VKParseCommandContext	\
		VKParseVKWindowContext

# Syntax tree classes
JAVAFILES +=	Commandable						\
		VKAction VKActor VKBlockAction VKBooleanCodeType	\
		VKBooleanType VKCodeDesc VKCodeType VKColorType		\
		VKCommand VKCommandBody VKCommandDefinition		\
		VKCommandName VKCompilationUnit VKConstants		\
		VKContext VKFieldListAction				\
		VKDateType VKDefaultCommand VKDefinition		\
		VKDefinitionCollector VKStringCodeType			\
		VKEnvironment VKExternAction VKFieldList		\
		VKFixnumCodeType VKFixnumType				\
		VKHelpUtils VKImageType VKInsert			\
		VKInsertParser VKIntegerCodeType			\
		VKIntegerType VKInternAction VKKjcTypeFactory		\
		VKLatexPrintWriter VKListDesc VKMenuDefinition		\
		VKMethodAction VKMonthType VKPhylum			\
		VKPrettyPrinter VKStdType VKStringType VKTextType	\
		VKTimeType VKTimestampType VKTopLevel VKTrigger		\
		VKType VKTypeFactory VKTypeDefinition VKUtils		\
		VKWeekType VKWindow VKLocalizationWriter		\
		VKMessageDefinition

MESSAGES =	BaseMessages

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
