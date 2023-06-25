package gtorr.Tracker;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Host {

    @Id
    public String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
