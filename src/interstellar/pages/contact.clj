(ns interstellar.pages.contact
  (:require [bouncer.core :as b]
            [bouncer.validators :as v]
            [clojure.tools.logging :as log]
            [interstellar.utils :refer [settings]]
            [postal.core :refer [send-message]]
            [ring.util.response :refer [redirect]]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Model
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn validate-message [params]
  (first
    (b/validate
      params
      :name v/required
      :email v/required
      :message v/required)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn make-sender-notification [username email message]
  (let [chinese-date (.format (java.text.DateFormat/getDateInstance
                                java.text.DateFormat/FULL
                                (java.util.Locale. "zh"))
                              (java.util.Date.))]
    {:from (settings :from_email)
     :to [email]
     :subject "谢谢联系"
     :body (str "亲爱的" username ":\n\n"
                "我们已收到您在" chinese-date "的提问:\n\n"
                 message "\n\n"
                 "我们会尽快联系您!")}))

(defn make-forward-email [username email message]
  {:from (settings :from_email)
   :to [(settings :to_email)]
   :subject (str "Question from " username "(" email ")")
   :body message})


(def email-settings
  {:host (settings :smtp)
   :user (settings :username)
   :pass (settings :app_password)
   :ssl (settings :ssl)})

(defn send-emails [username email message]
  "Sends an email to us and also sends a notification to the enquirer."
  (do
    (send-message
      email-settings
      (make-sender-notification username email message))
    (send-message
      email-settings
      (make-forward-email username email message))))

(defn handle-message [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (->
      (log/error errors)
      (redirect "/"))
    (do
      (let [{:keys [name email message]} params]
        (future (send-emails name email message)))
      (redirect "/"))))
