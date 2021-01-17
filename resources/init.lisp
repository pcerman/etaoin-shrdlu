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

(DEFUN SUBST (X Y Z)
  (COND ((EQ Z Y) X) ; if item eq to y, replace.
        ((ATOM Z) Z) ; if no substructure, return arg
        ((CONS (SUBST X Y (CAR Z)) ; otherwise recurse
               (SUBST X Y (CDR Z))))))

(DEFUN SUBLIS (X Y)
  (COND ((ATOM Y) (PROGV '(AP) (LIST (ASSOC Y X))
                    (COND (AP (CDR AP)) (Y))))
        ((PROGV '(CA CD)
                (LIST (SUBLIS X (CAR Y))
                      (SUBLIS X (CDR Y)))
           (COND ((AND (EQ CA (CAR Y)) (EQ CD (CDR Y)))
                      Y)
                 ((CONS CA CD)))))))
