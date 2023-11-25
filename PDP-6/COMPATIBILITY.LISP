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

;;; compatibility with PDP-6 functions

(defun alloc fexpr (arg)
  nil)

;;(defun fasload fexpr (args)
;;  (apply 'load args))

(setq *rest nil)

(defun *rest (x)
  (setq *rest x))

;;; ********************************************************************
;;; old input/output version

(setq uread nil)
(setq infile nil)
(setq ^q nil)

(setq *read* (function read))
(setq *readch* (function readch))
(setq *readline* (function readline))

(defun read cnt
  (cond ((and ^Q infile)
             (cond ((= cnt 0) (funcall *read* infile))
                   ((= cnt 1) (funcall *read* infile (arg 1)))
                   ((= cnt 2) (funcall *read* infile (arg 2)))
                   ((error "read - expected most 2 args"))))
        ((= cnt 2) (funcall *read* (arg 1) (arg 2)))
        ((= cnt 1) (funcall *read* (arg 1)))
        ((= cnt 0) (funcall *read*))
        ((error "read - expected most 2 args"))))

(defun readch cnt
  (cond ((and ^Q infile)
             (cond ((= cnt 0) (funcall *readch* infile))
                   ((= cnt 1) (funcall *readch* infile (arg 1)))
                   ((= cnt 2) (funcall *readch* infile (arg 2)))
                   ((error "readch - expected most 2 args"))))
        ((= cnt 2) (funcall *readch* (arg 1) (arg 2)))
        ((= cnt 1) (funcall *readch* (arg 1)))
        ((= cnt 0) (funcall *readch*))
        ((error "readch - expected most 2 args"))))

(defun readline cnt
  (cond ((and ^Q infile)
             (cond ((= cnt 0) (funcall *readline* infile))
                   ((= cnt 1) (funcall *readline* infile (arg 1)))
                   ((= cnt 2) (funcall *readline* infile (arg 2)))
                   ((error "readline - expected most 2 args"))))
        ((= cnt 2) (funcall *readline* (arg 1) (arg 2)))
        ((= cnt 1) (funcall *readline* (arg 1)))
        ((= cnt 0) (funcall *readline*))
        ((error "readline - expected most 2 args"))))

(defun uread fexpr (file)
  (or (null file)
      (= (length file) 4)
      (error "uread - expected 4 args"))
  (cond ((null file) (or (null infile)
                         (progn (close infile)
                                (setq uread nil)
                                (setq infile nil))))
        ((not infile)
             (setq uread file)
             (setq infile (openi (list (cadddr file) (car file)))))
        ((not (equal infile file))
             (close infile)
             (setq uread file)
             (setq infile (openi (list (cadddr file) (car file))))))
  uread)

;;; ********************************************************************
;;; Various DECLARE specification

(setq ^a nil)

(setq *declare-special* nil)
(setq *declare-expr* nil)
(setq *declare-fexpr* nil)
(setq *declare-print* nil)
(setq *declare-macros* nil)

(defun declare fexpr (args)
  (do ((ls args (cdr ls)))
      ((null ls) nil)
    (cond ((not (atom (car ls)))
               (cond ((eq (caar ls) 'genprefix))
                     ((eq (caar ls) 'special)
                          ;; (mapc (lambda (var)
                          ;;         (cond ((not (boundp var)) (bound var nil))))
                          ;;       (cdar ls))
                          (setq *declare-special* (set-union (cdar ls) *declare-special*)))
                     ((eq (caar ls) 'unspecial)
                          (setq *declare-special* (set-diff *declare-special* (cdar ls))))
                     ((eq (caar ls) '*expr)
                          (setq *declare-expr* (set-union (cdar ls) *declare-expr*)))
                     ((eq (caar ls) '*fexpr)
                          (setq *declare-fexpr* (set-union (cdar ls) *declare-fexpr*)))
                     ((eq (caar ls) 'print)
                          (setq *declare-print* (eval (cadar ls))))
                     ((eq (caar ls) 'macros)
                          (setq *declare-macros* (cdar ls)))
                     ((error (cons 'declare ls)))))
          ((error (cons 'declare ls))))))

(defun ioc fexpr (args)
  (do ((ls args (cdr ls)))
      ((null ls))
    (cond ((eq (car ls) 'c) (setq ^D nil))
          ((eq (car ls) 'd) (setq ^D t))
          ((eq (car ls) 's) (setq ^Q nil))
          ((eq (car ls) 'q) (setq ^Q t))
          ((eq (car ls) 'v) (setq ^W nil))
          ((eq (car ls) 'w) (setq ^W t))
          ((error (cons 'ioc ls))))))

(defun suspend nil
  nil)

(defun cursorpos cmd
  nil)

(defun pagebporg nil
  0)
