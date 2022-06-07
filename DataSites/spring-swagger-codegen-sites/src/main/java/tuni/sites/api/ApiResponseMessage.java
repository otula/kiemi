/**
 * Copyright 2020 Tampere University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tuni.sites.api;

import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * 
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-18T18:21:36.637143+03:00[Europe/Helsinki]")
@javax.xml.bind.annotation.XmlRootElement
public class ApiResponseMessage {
	/** response code */
    public static final int ERROR = 1;
    /** response code */
    public static final int WARNING = 2;
    /** response code */
    public static final int INFO = 3;
    /** response code */
    public static final int OK = 4;
    /** response code */
    public static final int TOO_BUSY = 5;

    int code;
    String type;
    String message;

    /**
     * 
     */
    public ApiResponseMessage(){
    	// nothing needed
    }

    /**
     * 
     * @param code
     * @param message
     */
    public ApiResponseMessage(int code, String message){
        this.code = code;
        switch(code){
        case ERROR:
            setType("error");
            break;
        case WARNING:
            setType("warning");
            break;
        case INFO:
            setType("info");
            break;
        case OK:
            setType("ok");
            break;
        case TOO_BUSY:
            setType("too busy");
            break;
        default:
            setType("unknown");
            break;
        }
        this.message = message;
    }

    /**
     * 
     * @return code
     */
    @XmlTransient
    public int getCode() {
        return code;
    }

    /**
     * 
     * @param code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * 
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
