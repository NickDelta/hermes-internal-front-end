import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';

class ErrorView extends PolymerElement {

    static get template() {
        return html`
<style>
                :host {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 1em;
                }
            
                :host()
        </style>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/normalize/5.0.0/normalize.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.3/css/bootstrap.min.css">
<div class="container" style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);">
 <div class="row">
  <div class="col-md-6 align-self-center" style="align-self: center;">
   <img style="max-width: 80%; max-height: 80%;" id="image">
  </div>
  <div class="col-md-6 align-self-center">
   <h1 style="font-family: Arial,sans-serif; font-size: 7.5em; margin: 15px 0px; font-weight: bold;" id="errorCode">[[Code]]</h1>
   <h2 style="font-family: Arial,sans-serif; font-weight: bold;" id="errorShortText">[[Friendly short text]]</h2>
   <p style="font-family: Arial,sans-serif" id="errorDetails">[[Friendly explanation]]</p>
   <vaadin-button theme="primary" id="homeButton">
     Home 
   </vaadin-button>
  </div>
 </div>
</div>
`;
    }

    static get is() {
        return 'error-view';
    }

    static get properties() {
        return {
            // Declare your properties here.
        };
    }

}

customElements.define(ErrorView.is, ErrorView);
