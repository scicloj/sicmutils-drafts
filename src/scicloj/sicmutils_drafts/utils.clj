(ns scicloj.sicmutils-drafts.utils
  (:require [notespace.paths]
            [notespace.repo]
            [clojure.string :as string]))

(defn ns->toc-link
  "Given a title and anamespace,
  provide the appropriate Github Pages link
  that should be added to the Table of Contents."
  ([title]
   (ns->toc-link title *ns*))
  ([title a-namespace]
   (format "[%s](%s/%s)"
           title
           (-> (notespace.repo/repo-url)
               (string/replace #"github.com/scicloj"
                               "scicloj.github.io"))
           (-> a-namespace
               notespace.paths/ns->target-path
               (string/replace #"^docs/"
                               "")
               (string/replace #"/index.html$"
                               "")))))
