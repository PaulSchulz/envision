(ns clojurewerkz.envisioncljs.line_chart
  (:require-macros [schema.macros :as sm])
  (:require [reagent.core            :as reagent :refer [atom]]
            [clojure.set             :as set]
            [schema.core             :as s]

            [clojurewerkz.envisioncljs.chart_config :as cfg]
            [clojurewerkz.envisioncljs.dimple       :as dimple]
            [clojurewerkz.envisioncljs.utils        :as u]))

(enable-console-print!)

(sm/defrecord LineChartState
    [^{:s s/Any}                 chart
     ^{:s s/Bool}                did-unmount])

(defn validate-line-chart-state
  [a]
  (s/validate LineChartState a))

(defn make-empty-line-chart-state
  []
  (LineChartState. nil false))

(defn- init-line-chart
  [this line-chart-config line-chart-state]
  (let [chart   (dimple/make-chart (u/dom-node this)
                                   (sm/safe-get line-chart-config :width)
                                   (sm/safe-get line-chart-config :height)
                                   )]

    (validate-line-chart-state
     (swap! line-chart-state #(assoc %
                                :chart chart)))

    (-> chart
        (dimple/set-data     (sm/safe-get line-chart-config :data))
        (dimple/add-axis     :category "x"
                             (sm/safe-get line-chart-config :x)
                             :order-rule (sm/safe-get line-chart-config :x-order))

        (dimple/add-axis     :measure "y" (sm/safe-get line-chart-config :y))

        (dimple/add-series   nil
                             (sm/safe-get line-chart-config :series-type)
                             :interpolation (sm/safe-get line-chart-config :interpolation))
        (dimple/set-bounds   (sm/safe-get line-chart-config :top-x)
                             (sm/safe-get line-chart-config :top-y)
                             (sm/safe-get line-chart-config :chart-width)
                             (sm/safe-get line-chart-config :chart-height)
                             )
        (dimple/draw))
    ))

(defn line-chart
  [line-chart-config line-chart-state]
  (with-meta (fn []
               (let [a @line-chart-state]
                 [:div {:class "envision-chart"
                        :key   (sm/safe-get line-chart-config :id)} ""]))
    {:component-did-mount (fn [this]
                            (init-line-chart this
                                             line-chart-config
                                             line-chart-state
                                             ))}))

(defn line-chart-app
  []
  (fn []
    (let [data (js->clj js/renderData :keywordize-keys true)]
      [:div
       (for [config data]
         [(line-chart
           (cfg/make-chart-config config)
           (atom (make-empty-line-chart-state))
           )])])))

(reagent/render-component [line-chart-app] (.getElementById js/document "app"))
