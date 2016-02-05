(ns interstellar.routes.home
  (:require [interstellar.layout :as layout]
            [interstellar.pages.contact :as contact]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [clojure.tools.logging :as log]))

(defn home-page [request]
  (layout/render
    "new_base.html" {}))

(defroutes home-routes
  (GET "/" request (home-page request))
  (POST "/" request (contact/handle-message request)))
