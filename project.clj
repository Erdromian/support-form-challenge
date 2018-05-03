(defproject support-form-challenge "0.1.0-SNAPSHOT"
  :description "An example support form webpage for Banzai"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]]
  :main ^:skip-aot support-form-challenge.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
