import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MensajesService } from '../../../../core/services/mensajes.service';
import { MetodopagoService } from '../../../admin/services/metodo-pago.service';
import { FormValidationUtils } from '../../../../utils/form-validation-utils';
import { RouteDataService } from '../../../../core/services/route-data.service';
import { CompraService } from '../../services/compra.service';
import { CarritoCounterService } from '../../../../core/services/carrito-counter.service';

@Component({
  selector: 'app-pago',
  templateUrl: './pago.component.html',
  styleUrls: ['./pago.component.css']
})
export class PagoComponent implements OnInit {

  // Variables principales de los datos del carrito
  datoCompra: any = null;
  productos: any[] = [];
  metodos: any[] = [];
  metodoPagoSeleccionado: any = null;
  total: number = 0;
  
  // Variables de pago
  transaccionCreada: any = null;
  qrUrl: string | null = null;
  mostrandoPago: boolean = false;
  pagoSimulado: boolean = false;
  
  // Formulario para Visa
  form!: FormGroup;
  frmValidationUtils!: FormValidationUtils;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private mensaje: MensajesService,
    private metodoPagoService: MetodopagoService,
    private routeDataService: RouteDataService,
    private compraService: CompraService,
    private carritoCounterService: CarritoCounterService
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    // Obtener datos del carrito desde RouteDataService
    this.datoCompra = this.routeDataService.getData();
    
    if (!this.datoCompra) {
      this.mensaje.showMessageError('No hay datos de compra disponibles');
      this.router.navigate(['/client/carrito']);
      return;
    }

    this.productos = this.datoCompra.productos || [];
    this.metodos = this.datoCompra.metodos || [];
    this.total = this.datoCompra.total || 0;

    // Pre-seleccionar el método de pago del carrito
    if (this.datoCompra.metodoPagoId) {
      const metodoSeleccionado = this.metodos.find(m => m.id === this.datoCompra.metodoPagoId);
      if (metodoSeleccionado) {
        this.seleccionarMetodo(metodoSeleccionado);
      }
    }

    console.log('Datos de compra recibidos:', this.datoCompra);
  }

  initForm(): void {
    this.form = this.formBuilder.group({
      metodoPago: ['', Validators.required],
      // Campos para Visa
      cardNumber: ['', [Validators.required, Validators.pattern(/^\d{16}$/)]],
      cardNombre: ['', Validators.required],
      cardApellido: ['', Validators.required],
      cardCvv: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]],
      cardCuotas: ['1', Validators.required],
      cardExpMonth: ['', [Validators.required, Validators.min(1), Validators.max(12)]],
      cardExpYear: ['', [Validators.required, Validators.min(2024)]]
    });

    this.frmValidationUtils = new FormValidationUtils(this.form);
  }

  seleccionarMetodo(metodo: any): void {
    this.metodoPagoSeleccionado = metodo;
    this.form.patchValue({ metodoPago: metodo.id });
    console.log('Método seleccionado:', metodo);
  }

  // Método para simular pago con Yape/Plin
  simularPagoYape(): void {
    if (!this.metodoPagoSeleccionado) {
      this.mensaje.showMessageError('Selecciona un método de pago');
      return;
    }

    const metodoTexto = this.metodoPagoSeleccionado.tipo.toLowerCase();
    if (!metodoTexto.includes('yape') && !metodoTexto.includes('plin')) {
      this.mensaje.showMessageError('Método de pago no válido para Yape/Plin');
      return;
    }

    this.mensaje.showMessageSuccess('Simulando pago con Yape/Plin...');
    this.mensaje.showLoading();
    
    // Generar código QR simulado
    const payload = `Pago: S/.${this.total} - Compra farmacia`;
    this.qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${encodeURIComponent(payload)}`;
    this.mostrandoPago = true;
    this.mensaje.closeLoading();

    // Simular proceso de pago de 3 segundos
    setTimeout(() => {
      this.confirmarPago();
    }, 3000);
  }

  // Método para simular pago con Visa
  simularPagoVisa(): void {
    if (!this.metodoPagoSeleccionado) {
      this.mensaje.showMessageError('Selecciona un método de pago');
      return;
    }

    const metodoTexto = this.metodoPagoSeleccionado.tipo.toLowerCase();
    if (!metodoTexto.includes('visa')) {
      this.mensaje.showMessageError('Método de pago no válido para Visa');
      return;
    }

    // Validar campos de tarjeta
    if (!this.form.get('cardNumber')?.value || !this.form.get('cardNombre')?.value || 
        !this.form.get('cardApellido')?.value || !this.form.get('cardCvv')?.value) {
      this.mensaje.showMessageError('Complete todos los datos de la tarjeta');
      return;
    }

    this.mensaje.showMessageSuccess('Procesando pago con tarjeta Visa...');
    this.mensaje.showLoading();
    this.mostrandoPago = true;

    // Simular proceso de pago de 4 segundos
    setTimeout(() => {
      this.mensaje.closeLoading();
      this.confirmarPago();
    }, 4000);
  }

  // Confirmar pago y completar la compra
  confirmarPago(): void {
    if (this.pagoSimulado) {
      return; // Evitar doble ejecución
    }

    this.pagoSimulado = true;
    this.mensaje.showLoading();

    // Crear objeto de compra final
    const datoFinal = {
      usuarioId: this.datoCompra.usuarioId,
      metodoPagoId: this.metodoPagoSeleccionado?.id || this.datoCompra.metodoPagoId,
      detalleCompra: this.datoCompra.detalleCompra,
    };

    // Ejecutar la compra usando el servicio
    this.compraService.save(datoFinal).subscribe({
      next: (res) => {
        this.mensaje.showMessageSuccess('¡Compra realizada con éxito!');
        // Resetear el contador del carrito
        this.carritoCounterService.updateCount(0);
      },
      error: (err) => {
        this.mensaje.showMessageErrorObservable(err);
        this.pagoSimulado = false; // Permitir reintentar
      },
      complete: () => {
        this.mensaje.closeLoading();
        // Navegar a la página de compras después de un delay
        setTimeout(() => {
          this.router.navigate(['/client/compras']);
        }, 2000);
      }
    });
  }

  // Regresar al carrito
  regresarCarrito(): void {
    // Restaurar los datos en el servicio para que el carrito no esté vacío
    this.router.navigate(['/client/carrito']);
  }

  // Cancelar el proceso de pago
  cancelarPago(): void {
    this.regresarCarrito();
  }

  // Métodos auxiliares para verificar tipo de método de pago
  esYapeOPlin(): boolean {
    if (!this.metodoPagoSeleccionado) return false;
    const tipo = this.metodoPagoSeleccionado.tipo.toLowerCase();
    return tipo.includes('yape') || tipo.includes('plin');
  }

  esVisa(): boolean {
    if (!this.metodoPagoSeleccionado) return false;
    const tipo = this.metodoPagoSeleccionado.tipo.toLowerCase();
    return tipo.includes('visa');
  }
}