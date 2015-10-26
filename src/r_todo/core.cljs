(ns ^:figwheel-always r-todo.core
    (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(def todos (atom {0 {:id 0 :text "Test Message" :done false}}))
(def todos-filter (atom {:done nil}))
(def pre-save (atom []))

(defn to-do-item [{:keys [id text done]}] 
  [:div.row 
   [:div.col-md-4.col-md-offset-4.input-group 
    [:div.checkbox
     [:label {:class (if (true? done) "text-muted")}
      [:input {:type "checkbox" :id id :checked done :on-change #(swap! todos assoc-in [id :done] (-> % .-target .-checked) )}] text]]
    [:div.input-group-btn 
     [:button.close {:type "button" :on-click #(swap! todos dissoc  id)} "x"]]]])

(defn to-do-filter [] 
  (let [toggle (fn [state] {:on-click #(reset! todos-filter {:done state}) :class (if (= (:done @todos-filter) state) "active")})]
  [:div.btn-group.btn-group-sm
   [:button.btn.btn-default (toggle nil) "All" ]
   [:button.btn.btn-default (toggle false) "Active"]
   [:button.btn.btn-default (toggle true) "Completed"]]
  ))

(defn to-do []
  (let [val (atom nil)] 
    (fn [] 
      [:div
   [:div.row
    [:div.col-md-4.col-md-offset-4
      [:div.col-md-10 
        [:input.form-control.center-block {:type "text" :value @val :on-change #(reset! val (-> % .-target .-value))}]]
      [:button.btn.btn-default.col-md-2 
       {:type :submit 
        :on-click #(if-not (empty? @val) (do (swap! todos assoc (count @todos) {:text @val :id (count @todos) :done false}) (reset! val "")))} "Add"]]]
  [:div.row
   [:div.col-md-offset-5 [to-do-filter]]]
  (for [[k item] @todos]
      ^{:key (:id item)}
      [to-do-item item])
  [:div.row
   [:div.col-md-4.col-md-offset-4 
    [:div.col-md-4 (str "complete " (count (filter #(true? (:done (second %))) @todos)))]]]])))

(reagent/render [to-do]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

