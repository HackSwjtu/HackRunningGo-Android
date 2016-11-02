package top.catfish.hackrunninggo.Utils;

import java.io.Serializable;
import java.util.Map;

/*
 * Created by Catfish on 2016/11/2.
 */

public class SerializableMap implements Serializable {

        private Map<String,String> map;

        public Map<String, String> getMap() {
            return map;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }
}
