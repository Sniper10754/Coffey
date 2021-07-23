module Coffey.library.core.main {
    requires java.base;
    requires kotlin.stdlib;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires klaxon;
    requires org.apache.commons.io;

    exports org.coffey;
}