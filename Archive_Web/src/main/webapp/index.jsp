<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Archive Management</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        .section {
            margin-bottom: 20px;
        }
        .section h2 {
            margin-bottom: 10px;
        }
        .section input, .section button {
            margin-right: 10px;
            margin-bottom: 10px;
        }
        .result {
            margin-top: 10px;
            padding: 10px;
            border: 1px solid rgba(151, 66, 66, 0.8);
            background-color: #ffffff;
        }
        .hidden {
            display: none;
        }
        .file-list div {
            cursor: pointer;
            padding: 5px;
            border: 1px solid rgba(151, 66, 66, 0.8);
            margin-bottom: 5px;
            background-color: #ffffff;
        }
        .file-list div:hover {
            background-color: #f0f0f0;
        }
        .archive-item {
            margin-bottom: 5px;
        }
        .archive-item .files {
            margin-left: 20px;
            margin-top: 5px;
            padding-left: 10px;
            border-left: 2px solid #ccc;
            font-size: 0.9em;
        }
        .archive-item .files div {
            padding: 3px;
            border: none;
            background-color: #f9f9f9;
        }
    </style>
</head>
<body>
<h1>Archive Management</h1>

<!-- Создание архива -->
<div class="section">
    <h2>Create Archive</h2>
    <button onclick="toggleInput('createArchiveInput')">Create Archive</button>
    <div id="createArchiveInput" class="hidden">
        <input type="text" id="createArchiveName" placeholder="Archive Name">
        <button onclick="createArchive()">Submit</button>
    </div>
</div>

<!-- Удаление архива -->
<div class="section">
    <h2>Delete Archive</h2>
    <button onclick="toggleInput('deleteArchiveInput')">Delete Archive</button>
    <div id="deleteArchiveInput" class="hidden">
        <input type="text" id="deleteArchiveName" placeholder="Archive Name">
        <button onclick="deleteArchive()">Submit</button>
    </div>
</div>

<!-- Добавление файла в архив -->
<div class="section">
    <h2>Add File to Archive</h2>
    <button onclick="toggleInput('addFileInput')">Add File</button>
    <div id="addFileInput" class="hidden">
        <input type="text" id="addFileArchiveName" placeholder="Archive Name">
        <input type="text" id="addFileName" placeholder="File Name">
        <button onclick="addFileToArchive()">Submit</button>
    </div>
</div>

<!-- Удаление файла из архива -->
<div class="section">
    <h2>Delete File from Archive</h2>
    <button onclick="toggleInput('deleteFileInput')">Delete File</button>
    <div id="deleteFileInput" class="hidden">
        <input type="text" id="deleteFileArchiveName" placeholder="Archive Name">
        <input type="text" id="deleteFileName" placeholder="File Name">
        <button onclick="deleteFileFromArchive()">Submit</button>
    </div>
</div>

<!-- Список архивов -->
<div class="section">
    <h2>Archives List</h2>
    <div id="archiveList" class="file-list"></div>
</div>

<script>
    $(document).ready(function() {
        loadArchives();
    });

    function clearInputFields() {
        $('#createArchiveName').val('');
        $('#deleteArchiveName').val('');
        $('#addFileArchiveName').val('');
        $('#addFileName').val('');
        $('#deleteFileArchiveName').val('');
        $('#deleteFileName').val('');
        $('#getArchiveContentArchiveName').val('');
    }

    function loadArchives() {
        $.get('archive', {action: 'listArchives'}, function(response) {
            const archiveList = $('#archiveList');
            archiveList.empty();
            if (response.length === 0) {
                const noArchivesElement = $('<div></div>').text('No archives found.');
                archiveList.append(noArchivesElement);
            } else {
                response.forEach(archive => {
                    const archiveItem = $('<div class="archive-item"></div>');
                    const archiveNameElement = $('<div></div>').text(archive.name);
                    const filesContainer = $('<div class="files hidden"></div>');

                    archiveNameElement.on('click', () => {
                        filesContainer.toggleClass('hidden');
                        if (!filesContainer.hasClass('hidden') && filesContainer.is(':empty')) {
                            loadFiles(archive.name, filesContainer);
                        }
                    });

                    archiveItem.append(archiveNameElement);
                    archiveItem.append(filesContainer);
                    archiveList.append(archiveItem);
                });
            }
        }).fail(function(error) {
            toastr.error('Error: ' + error.responseJSON.error);
        });
    }

    function loadFiles(archiveName, container) {
        if (!archiveName) {
            toastr.error('Error: Archive name is not specified.');
            return;
        }

        $.get('archive', {action: 'listFilesInArchive', archiveName: archiveName}, function(response) {
            container.empty();

            if (Array.isArray(response)) {
                if (response.length === 0) {
                    const noFilesElement = $('<div></div>').text('No files found in this archive.');
                    container.append(noFilesElement);
                } else {
                    response.forEach(file => {
                        const fileElement = $('<div></div>').text(file);
                        container.append(fileElement);
                    });
                }
            } else {
                toastr.error('Error: Invalid data format received from server.');
            }
        }).fail(function(error) {
            toastr.error('Error: ' + error.responseJSON.error);
        });
    }

    function toggleInput(elementId) {
        $('#' + elementId).toggleClass('hidden');
    }

    function createArchive() {
        const archiveName = $('#createArchiveName').val();
        $.post('archive', {action: 'createArchive', archiveName: archiveName}, function(response) {
            toastr.success('Archive created successfully.');
            loadArchives();
            clearInputFields();
        }).fail(function(error) {
            toastr.error('Error: ' + error.responseJSON.error);
        });
    }

    function deleteArchive() {
        const archiveName = $('#deleteArchiveName').val();
        $.post('archive', {action: 'deleteArchive', archiveName: archiveName}, function(response) {
            toastr.success('Archive deleted successfully.');
            loadArchives();
            clearInputFields();
        }).fail(function(error) {
            toastr.error('Error: ' + error.responseJSON.error);
        });
    }

    function addFileToArchive() {
        const archiveName = $('#addFileArchiveName').val();
        const fileName = $('#addFileName').val();
        $.post('archive', {action: 'addFileToArchive', archiveName: archiveName, fileName: fileName}, function(response) {
            toastr.success('File added successfully.');
            loadArchives();
            clearInputFields();
        }).fail(function(error) {
            toastr.error('Error: ' + error.responseJSON.error);
        });
    }

    function deleteFileFromArchive() {
        const archiveName = $('#deleteFileArchiveName').val();
        const fileName = $('#deleteFileName').val();
        $.post('archive', {action: 'deleteFileFromArchive', archiveName: archiveName, fileName: fileName}, function(response) {
            toastr.success('File deleted successfully.');
            loadArchives();
            clearInputFields();
        }).fail(function(error) {
            toastr.error('Error: ' + error.responseJSON.error);
        });
    }
</script>
</body>
</html>