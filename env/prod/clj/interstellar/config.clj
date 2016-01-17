(ns interstellar.config
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[interstellar started successfully]=-"))
   :middleware identity})
