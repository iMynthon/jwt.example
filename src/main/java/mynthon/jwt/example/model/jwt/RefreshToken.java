package mynthon.jwt.example.model.jwt;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RefreshToken implements Serializable {

    private String id;

    private String userId;

    private String value;
}
