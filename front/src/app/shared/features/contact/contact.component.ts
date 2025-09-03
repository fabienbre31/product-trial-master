import { Component } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.scss'],
})
export class ContactComponent {
  submitted = false;

  contactForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    message: ['', [Validators.required, Validators.maxLength(300)]],
  });

  constructor(private fb: FormBuilder) {}

  onSubmit() {
    this.submitted = true;

    if (this.contactForm.valid) {
      alert('Demande de contact envoyée avec succès'); //On peut aussi faire un vrai module mais l'alerte suffit
      this.contactForm.reset();
      this.submitted = false;
    }
  }

  get email() {
    return this.contactForm.get('email');
  }

  get message() {
    return this.contactForm.get('message');
  }
}
