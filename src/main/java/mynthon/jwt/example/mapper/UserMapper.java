package mynthon.jwt.example.mapper;
import mynthon.jwt.example.model.User;
import mynthon.jwt.example.web.dto.request.UserRequest;
import mynthon.jwt.example.web.dto.response.UserResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    User requestToEntity(UserRequest request);

    UserResponse entityToResponse(User user);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "roles",ignore = true)
    void updateEntity(@MappingTarget User destination, User root);
}
