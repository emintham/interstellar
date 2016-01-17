(ns interstellar.config
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [interstellar.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[interstellar started successfully using the development profile]=-"))
   :middleware wrap-dev})
