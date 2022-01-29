<%@page contentType="text/html" pageEncoding="UTF-8" %>
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

    <%
      pl.polsl.lab.szymonbotor.notemanager.controller.UserController userCont = new pl.polsl.lab.szymonbotor.notemanager.controller.UserController(request.getSession());
    pl.polsl.lab.szymonbotor.notemanager.model.AES aes = userCont.getAES();
      pl.polsl.lab.szymonbotor.notemanager.controller.NoteController noteCont = new pl.polsl.lab.szymonbotor.notemanager.controller.NoteController(request.getSession());
      pl.polsl.lab.szymonbotor.notemanager.entities.Note note = noteCont.getNote();

    
    %>

      <div class="container-md">

        <a href="/NoteManager" class="row col-auto btn btn-secondary mt-2 mb-5">Home</a>

        <form method="POST">
          <div class="row container mb-4">
            <h3 class="row mb-3">
              <%= note.getName() %>
            </h3>
            <textarea name="content" id="content" class="form-control row" rows="3"><%= noteCont.getDecryptedContent(aes) %></textarea>
          </div>

          <div class="row mb-5">
            <button type="submit" class="btn btn-success col-auto" name="save" value="true">Save</button>
          </div>

          <div class="row">
            <button type="submit" class="btn btn-danger col-auto" name="remove" value="true">Remove</button>
          </div>

        </form>
      </div>

      </div>
      <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
        crossorigin="anonymous"></script>
  </body>

  </html>