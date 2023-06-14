package site.nomoreparties.stellarburgers.pojo;

import java.util.Date;

public class Owner {
        public String name;
        public String email;
        public Date createdAt;
        public Date updatedAt;

        public Owner() {
        }

        public String getName() {
                return name;
        }

        public String getEmail() {
                return email;
        }

        public Date getCreatedAt() {
                return createdAt;
        }

        public Date getUpdatedAt() {
                return updatedAt;
        }
}
