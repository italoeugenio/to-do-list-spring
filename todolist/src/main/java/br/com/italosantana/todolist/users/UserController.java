package br.com.italosantana.todolist.users;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "user-controller", description = "Gerenciar Usuários")
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserRepository userRepository;

    @Operation(summary = "Cria um novo usuário",
            description = "Este endpoint permite criar um novo usuário. O nome de usuário deve ser único.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Modelo de usuário contendo as informações necessárias para a criação",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"username\": \"italoeugenio\",\n  \"name\": \"Ítalo\",\n  \"password\": \"123456\"\n}"))
            )
    )

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel){
        var user = this.userRepository.findByUsername(userModel.getUsername());
        if(user != null){
            System.out.println("Usurário já existe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
        }

        var passwordHashred = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());

        userModel.setPassword(passwordHashred);

        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.OK
        ).body(userCreated);
    }
}
