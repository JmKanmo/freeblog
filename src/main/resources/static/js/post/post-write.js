class PostWriteController extends UtilController {
    constructor() {
        super();
        this.postWriterEditor = this.getQuillEditor('post_write_editor');
        this.viewSourceBtn = document.getElementById("view-editor-source");
    }

    initPostWriteController() {
        this.initEventListener();
    }

    initEventListener() {
        this.viewSourceBtn.addEventListener("click", evt => {
            const wnd = this.openPopUp(500, 500, "HTML Source View", "new window");

            wnd.document.write(
                `<html>
                    <head>
                    <title>View HTML Source</title>
                    <style>
                        .code_view_title {
                            text-align:center;
                            color: #607d8b;
                            font-size: 18px;
                        }
                        
                         .code_container { 
                             width: 485px;
                             height: 455px;
                             text-align: left; 
                             white-space: pre-line; 
                             background-color: #f1f8e9; 
                             border: 1px solid #eeeeee;
                             color: #9c27b0; 
                             padding:10px;
                          }
                          
                         .code_container:before { 
                             content: ""; 
                             display: block; 
                             height: 1em; 
                             margin: 0 -5px -2em -5px; 
                        }
                    </style> 
                    </head>
                   
                    <body>                   
                        <h1 class="code_view_title">HTML Source View</h1>
                            <textarea class="code_container">
                               ${this.introEditor.root.innerHTML}
                            </textarea>
                    </body>
                </html>`);
        });
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const postWriteController = new PostWriteController();
    postWriteController.initPostWriteController();
});