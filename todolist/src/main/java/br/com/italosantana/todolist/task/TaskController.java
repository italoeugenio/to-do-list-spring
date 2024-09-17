package br.com.italosantana.todolist.task;

import br.com.italosantana.todolist.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/task")
@Tag(name = "task-controller", description = "Gerenciamento de tarefas")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @Operation(
            summary = "Cria uma nova tarefa",
            description = "Este endpoint permite criar uma nova tarefa associada ao usuário logado.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Modelo de tarefa contendo as informações necessárias para a criação",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo de Criação de Tarefa",
                                    value = "{\n  \"description\": \"Task com Spring Boot\",\n  \"title\": \"Gravação de aula de JAVA\",\n  \"priority\": \"ALTA\",\n  \"startAt\": \"2024-10-10T11:00:00\",\n  \"endAt\": \"2024-10-10T12:00:00\"\n}"
                            )
                    )
            )
    )


    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        System.out.println("Chegou na controller" + request.getAttribute("idUser"));
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de início deve ser menor do que a data de término");
        }

        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de início/término deve ser maior do que a data atual");
        }


        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @Operation(summary = "Listar tarefas do usuário", description = "Este endpoint retorna todas as tarefas associadas ao usuário logado.")
    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        return this.taskRepository.findByIdUser((UUID) idUser);
    }

    @Operation(
            summary = "Atualiza uma tarefa existente",
            description = "Este endpoint permite atualizar uma tarefa baseada no ID fornecido.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Modelo de tarefa contendo as informações a serem atualizadas",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo de Atualização de Tarefa",
                                    value = "{\n  \"description\": \"Atualização da descrição da tarefa\",\n  \"title\": \"Título Atualizado\",\n  \"startAt\": \"2024-10-10T11:00:00\",\n  \"endAt\": \"2024-10-10T12:00:00\",\n  \"priority\": \"MÉDIA\"\n}"
                            )
                    )
            )
    )

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request){
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tarefa não encontrada");
        }

        var idUser = request.getAttribute("idUser");

        if(!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário não tem permissão para alterar essa tarefa");
        }

        Utils.copyNullProperties(taskModel,task);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);
    }
}
