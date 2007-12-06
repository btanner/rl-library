/* A Bison parser, made by GNU Bison 2.3.  */

/* Skeleton interface for Bison's Yacc-like parsers in C

   Copyright (C) 1984, 1989, 1990, 2000, 2001, 2002, 2003, 2004, 2005, 2006
   Free Software Foundation, Inc.

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA 02110-1301, USA.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* Tokens.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
   /* Put the tokens into the symbol table, so that GDB and other debuggers
      know about them.  */
   enum yytokentype {
     RCSS_PCOM_INT = 258,
     RCSS_PCOM_REAL = 259,
     RCSS_PCOM_STR = 260,
     RCSS_PCOM_LP = 261,
     RCSS_PCOM_RP = 262,
     RCSS_PCOM_DASH = 263,
     RCSS_PCOM_TURN = 264,
     RCSS_PCOM_TURN_NECK = 265,
     RCSS_PCOM_KICK = 266,
     RCSS_PCOM_CATCH = 267,
     RCSS_PCOM_SAY = 268,
     RCSS_PCOM_UNQ_SAY = 269,
     RCSS_PCOM_SENSE_BODY = 270,
     RCSS_PCOM_SCORE = 271,
     RCSS_PCOM_MOVE = 272,
     RCSS_PCOM_CHANGE_VIEW = 273,
     RCSS_PCOM_COMPRESSION = 274,
     RCSS_PCOM_BYE = 275,
     RCSS_PCOM_DONE = 276,
     RCSS_PCOM_POINTTO = 277,
     RCSS_PCOM_ATTENTIONTO = 278,
     RCSS_PCOM_TACKLE = 279,
     RCSS_PCOM_CLANG = 280,
     RCSS_PCOM_EAR = 281,
     RCSS_PCOM_VIEW_WIDTH_NARROW = 282,
     RCSS_PCOM_VIEW_WIDTH_NORMAL = 283,
     RCSS_PCOM_VIEW_WIDTH_WIDE = 284,
     RCSS_PCOM_VIEW_QUALITY_LOW = 285,
     RCSS_PCOM_VIEW_QUALITY_HIGH = 286,
     RCSS_PCOM_ON = 287,
     RCSS_PCOM_OFF = 288,
     RCSS_PCOM_OUR = 289,
     RCSS_PCOM_OPP = 290,
     RCSS_PCOM_LEFT = 291,
     RCSS_PCOM_RIGHT = 292,
     RCSS_PCOM_EAR_PARTIAL = 293,
     RCSS_PCOM_EAR_COMPLETE = 294,
     RCSS_PCOM_CLANG_VERSION = 295,
     RCSS_PCOM_ERROR = 296
   };
#endif
/* Tokens.  */
#define RCSS_PCOM_INT 258
#define RCSS_PCOM_REAL 259
#define RCSS_PCOM_STR 260
#define RCSS_PCOM_LP 261
#define RCSS_PCOM_RP 262
#define RCSS_PCOM_DASH 263
#define RCSS_PCOM_TURN 264
#define RCSS_PCOM_TURN_NECK 265
#define RCSS_PCOM_KICK 266
#define RCSS_PCOM_CATCH 267
#define RCSS_PCOM_SAY 268
#define RCSS_PCOM_UNQ_SAY 269
#define RCSS_PCOM_SENSE_BODY 270
#define RCSS_PCOM_SCORE 271
#define RCSS_PCOM_MOVE 272
#define RCSS_PCOM_CHANGE_VIEW 273
#define RCSS_PCOM_COMPRESSION 274
#define RCSS_PCOM_BYE 275
#define RCSS_PCOM_DONE 276
#define RCSS_PCOM_POINTTO 277
#define RCSS_PCOM_ATTENTIONTO 278
#define RCSS_PCOM_TACKLE 279
#define RCSS_PCOM_CLANG 280
#define RCSS_PCOM_EAR 281
#define RCSS_PCOM_VIEW_WIDTH_NARROW 282
#define RCSS_PCOM_VIEW_WIDTH_NORMAL 283
#define RCSS_PCOM_VIEW_WIDTH_WIDE 284
#define RCSS_PCOM_VIEW_QUALITY_LOW 285
#define RCSS_PCOM_VIEW_QUALITY_HIGH 286
#define RCSS_PCOM_ON 287
#define RCSS_PCOM_OFF 288
#define RCSS_PCOM_OUR 289
#define RCSS_PCOM_OPP 290
#define RCSS_PCOM_LEFT 291
#define RCSS_PCOM_RIGHT 292
#define RCSS_PCOM_EAR_PARTIAL 293
#define RCSS_PCOM_EAR_COMPLETE 294
#define RCSS_PCOM_CLANG_VERSION 295
#define RCSS_PCOM_ERROR 296




#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef int YYSTYPE;
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif



