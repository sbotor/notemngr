<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html lang="en">

<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
    integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">

  <title>NoteManager</title>
</head>

<body>
      <div class="container-md">
        <h1 class="row mt-3">Note Manager</h1>

        <div class="container row">

        <% 
            pl.polsl.lab.szymonbotor.notemanager.controller.UserController userCont = new pl.polsl.lab.szymonbotor.notemanager.controller.UserController(request.getSession());
            pl.polsl.lab.szymonbotor.notemanager.entities.User user = userCont.getUser();
            pl.polsl.lab.szymonbotor.notemanager.model.AES aes = userCont.getAES();

            if (userCont.isAuthenticated()) { %>

            <h4 class="row">Logged in as: <%= user.getUsername() %> </h4>
            <div class="container row mt-4">
                <a href="/NoteManager/user" class="btn btn-primary col-auto">My notes</a>
            </div>
            <div class="container row mt-5">
                <a href="/NoteManager/logout" class="btn btn-outline-danger col-auto mb-3">Log out</a>
            </div>

        <% } else { %>
            <form method="POST" action="user" class="col-4 mt-3">
                <div class="row">
                    <h4 class="col-8">Log in</h4>
                </div>

                <div class="mb-3">
                    <label for="username">Username</label>
                    <input type="text" class="form-control" id="username" name="username">
                </div>
                <div class="mb-3">
                    <label for="password">Password</label>
                    <input type="password" class="form-control" id="password" name="password">
                </div>

                <button type="submit" class="btn btn-primary float-end">Log in</button>
            </form>

            <div class="row">
                <button class="col-auto btn btn-success" data-bs-toggle="modal" data-bs-target="#newUserModal">Create
                    a new user</button>
            </div>

            <!-- New user modal -->
            <form method="POST" action="newUser">
                <div class="modal fade" id="newUserModal" tabindex="-1" aria-labelledby="newUserModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="newUserModalLabel">New user</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <form method="POST" action="newUser">
                                    <div class="mb-3">
                                        <label for="newUsername">Username</label>
                                        <input type="text" id="newUsername" name="newUsername" class="form-control">
                                    </div>

                                    <div class="mb-3">
                                        <label for="pass1">Password</label>
                                        <input type="password" id="pass1" name="pass1" class="form-control">
                                    </div>

                                    <div class="mb-3">
                                        <label for="pass2">Repeat password</label>
                                        <input type="password" id="pass2" name="pass2" class="form-control">
                                    </div>

                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                        <button type="submit" class="btn btn-success">Create</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        <% } %>

        <div class="row">
            <a href="/NoteManager/generate" class="col-auto btn btn-secondary mt-5">Generate a password</a>
        </div>
        </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
        crossorigin="anonymous"></script>
</body>

</html>