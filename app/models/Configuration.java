/*
 *   Copyright 2016 ITM, S.A.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * `*   `[`http://www.apache.org/licenses/LICENSE-2.0`](http://www.apache.org/licenses/LICENSE-2.0)
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.Crypto;

import javax.persistence.*;


/**
 * Created by jandrad on 26/09/15.
 */
@Entity
public class Configuration extends Model {


    @Required
    @MaxSize(200)
    @Column(length = 200)
    public String apiURL;

    @Required
    @MaxSize(100)
    @Column(length = 100)
    public String apiUser;

    @Required
    @MaxSize(100)
    @Column(length = 100)
    public String apiPassword;

    public boolean validateCertificate;

    public Blob trustStore;

    @MaxSize(200)
    @Column(length = 20)
    public String trustStorePassword;

    @Required
    @MaxSize(50)
    @Column(length = 50)
    public String managerIp;

    @Required
    @MaxSize(100)
    @Column(length = 100)
    public String managerUser;

    @Required
    @MaxSize(100)
    @Column(length = 100)
    public String managerKeyLocation;

    @Required
    @MaxSize(100)
    @Column(length = 100)
    public String managerBinLocation;

    @Required
    @MaxSize(100)
    @Column(length = 100)
    public String managerCommand;

    private Configuration() {

    }

    public static Configuration generalConfiguration() {

        Configuration configuration = Configuration.find("").first();
        if (configuration == null) {
            configuration = new Configuration();
            configuration.save();
        }

        return configuration;
    }

    public void applyConfiguration(Configuration configuration) {
        apiURL = configuration.apiURL;
        apiUser = configuration.apiUser;
        apiPassword = configuration.apiPassword;
        validateCertificate = configuration.validateCertificate;
        managerIp = configuration.managerIp;
        managerUser = configuration.managerUser;
        managerKeyLocation = configuration.managerKeyLocation;
        managerBinLocation = configuration.managerBinLocation;
        managerCommand = configuration.managerCommand;
        trustStorePassword = configuration.trustStorePassword;


        if (configuration.trustStore!=null && configuration.trustStore.exists()) {
            if (trustStore!=null && trustStore.exists()) {
                trustStore.getFile().delete();
            }

            trustStore = configuration.trustStore;
        }
    }

    @PreUpdate
    @PrePersist
    public void encryptPassword() {
        if (apiPassword!=null) {
            apiPassword = Crypto.encryptAES(apiPassword);
        }
    }

    @PostLoad
    public void decryptPassword() {
        if (apiPassword!=null) {
            apiPassword = Crypto.decryptAES(apiPassword);
        }
    }
}
