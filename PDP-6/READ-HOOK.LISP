;;; -*- mode: lisp -*-

;;;  +---------------------------------------------------------------+
;;;  | etaoin-shrdlu: LISP interpreter for the SHRDLU project        |
;;;  |                                                               |
;;;  | Original source code is published in github resository:       |
;;;  | https://github.com/pcerman/etaoin-shrdlu.                     |
;;;  |                                                               |
;;;  | Copyright (c) 2021 Peter Cerman (https://github.com/pcerman)  |
;;;  |                                                               |
;;;  | This source code is released under Mozilla Public License 2.0 |
;;;  +---------------------------------------------------------------+

;;; read hook for SHRDLU

(DEFUN SYMBOL-DROP (SYM N)
  (COND ((NOT (SYMBOLP SYM)) SYM)
        ((< N 1) SYM)
        ((DO ((CHS (EXPLODE SYM) (CDR CHS))
              (N N (1- N)))
             ((ZEROP N) (IMPLODE CHS))))))

;; EXPANDS _ TO (THNV (READ))
;; EXPANDS A TO ASSERT
;; EXPANDS G TO GOAL
;; EXPANDS T TO (THTBF THTRUE)
;; EXPANDS ? TO (THV (READ))
;; EXPANDS E TO (THEV (READ))
;; EXPANDS R TO THRESTRICT
;; TREATS & - - & AS A COMMENT

(DEFUN THSVAR (VAR)
  (COND ((AND (SYMBOLP VAR) (EQ (GETCHAR VAR 1) '$) (> (FLATC VAR) 2))
             (PROGV '(CH2) NIL
               (SETQ CH2 (GETCHAR VAR 2))
               (COND ((EQ CH2 '?)
                          (LIST 'THV (THSVAR (SYMBOL-DROP VAR 2))))
                     ((EQ CH2 'E)
                          (LIST 'THEV (THSVAR (SYMBOL-DROP VAR 2))))
                     ((EQ CH2 '_)
                          (LIST 'THNV (THSVAR (SYMBOL-DROP VAR 2))))
                     ((EQ CH2 '&)
                          (DO ((CHS (CDDR (EXPLODE VAR)) (CDR CHS)))
                              ((OR (NULL CHS) (EQ (CAR CHS '&)))
                                   (OR (AND (NULL CHS) (SYMBOL-DROP VAR 2))
                                       (IMPLODE (CDR CHS))))))
                     ((EQ CH2 'T)
                          '(THTBF THTRUE))
                     ((EQ CH2 'R)
                          'THRESTRICT)
                     ((EQ CH2 'G)
                          'THGOAL)
                     ((EQ CH2 'A)
                          'THASSERT)
                     ((EQ CH2 'N)
                          (LIST 'THANUM (THSVAR (READLIST (CDDR (EXPLODE VAR))))))
                     (VAR))))

        ((NOT (ATOM VAR))
             (DO ((VAR VAR (CDR VAR))
                  (LAST (LIST NIL) (CDR LAST))
                  (NVAR NIL (OR NVAR LAST)))
                 ((ATOM VAR) (RPLACD LAST (THSVAR VAR)) (CDR NVAR))
               (COND ((AND (EQ (CAR VAR) '$E) (NOT (ATOM (CDR VAR))))
                          (RPLACD LAST (LIST (LIST 'THEV (THSVAR (CADR VAR)))))
                          (SETQ VAR (CDR VAR)))
                     ((RPLACD LAST (LIST (THSVAR (CAR VAR))))))))

        ((EQ VAR '$T)
             '(THTBF THTRUE))
        ((EQ VAR '$R)
             'THRESTRICT)
        ((EQ VAR '$G)
             'THGOAL)
        ((EQ VAR '$A)
             'THASSERT)
        (VAR)))
