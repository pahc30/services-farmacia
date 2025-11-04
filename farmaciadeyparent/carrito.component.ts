import { Component, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { MensajesService } from '../../../../core/services/mensajes.service';
import { CarritoService } from '../../services/carrito.service';
import { AuthJWTService } from '../../../../core/services/auth.service';
import { MetodopagoService } from '../../../admin/services/metodo-pago.service';
import { forkJoin, map } from 'rxjs';
import { FormValidationUtils } from '../../../../utils/form-validation-utils';
import { CompraService } from '../../services/compra.service';
import { CarritoCounterService } from '../../../../core/services/carrito-counter.service';
import { RouteDataService } from '../../../../core/services/route-data.service';

@Component({
  selector: 'app-carrito',
  templateUrl: './carrito.component.html',
  styleUrl: './carrito.component.css'
})
export class CarritoComponent {

  displayedColumns: string[] = [
    'id', 'codigo', 'nombre',
    'precio', 'cantidad', 'categoria', 'imagen', 'acciones'
  ];
  dataSource!: MatTableDataSource<any[]>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  total: number  = 0;
  productos: any[] = [];
  metodos : any[] = [];

  frmValidationUtils!: FormValidationUtils;
  form!: FormGroup;

  constructor(private formBuilder: FormBuilder,
    private mensaje: MensajesService,
    private carritoService: CarritoService,
    private metodoPagoService: MetodopagoService,
    private compraService: CompraService,
    private authService: AuthJWTService,
    private carritoCounterService: CarritoCounterService,
    private routeDataService: RouteDataService,
    private router: Router,) { }

  ngOnInit(): void {
    this.initForm();
    this.inicializarData();
  }

  initForm = () => {
    this.form = this.formBuilder.group({
      metodo: [null, [Validators.required]],
      // Campos adicionales para datos de tarjeta (serán usados en la página de pago)
      cardNumber: [null],
      cardNombre: [null],
      cardApellido: [null],
      cardCvv: [null],
      cardExpMonth: [null],
      cardExpYear: [null],
      cardCuotas: [null],
    });

    this.frmValidationUtils = new FormValidationUtils(this.form);
  }

  inicializarData = () => {
    const user = this.authService.getInfoUsuario();
    this.mensaje.showLoading();
    forkJoin({
      productos: this.carritoService.list(user.id),
      metodos: this.metodoPagoService.list(),
    })
      .pipe(
        map((res) => {
          this.productos = res.productos['dato'];
          // Mostrar solo los métodos permitidos en la UI cliente
          const allMetodos = res.metodos['dato'] || [];
          this.metodos = allMetodos.filter((m: any) => /yape|plin|visa/i.test(m.tipo));
          console.log('Métodos cargados:', this.metodos);
        })
        )
      .subscribe({
        next: (res) => { },
        error: (err) => {
          this.mensaje.showMessageErrorObservable(err);
        },
        complete: () => {
          this.mensaje.closeLoading();

          this.total = 0;
          this.productos.forEach((item: any) => {
            this.total += item.producto?.precio * item.cantidad;
          });
          this.dataSource = new MatTableDataSource(this.productos);
          this.dataSource.paginator = this.paginator;
          // Actualizar el contador con la cantidad actual de productos
          this.carritoCounterService.updateCount(this.productos.length);
        },
      });
  }

  // Método modificado: ahora navega a la página de pago en lugar de ejecutar directamente
  onClickPagar = () => {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const user = this.authService.getInfoUsuario();

    let detalle: any[] = [];
    this.productos.forEach((item) => {
      detalle.push({
        productoId: item.producto.id,
        cantidad: item.cantidad,
        carritoCompraId: item.id,
      });
    });

    const datoCompra = {
      usuarioId: user.id,
      metodoPagoId: this.form.get('metodo')?.value,
      detalleCompra: detalle,
      total: this.total,
      productos: this.productos,
      metodos: this.metodos
    };

    // Guardar datos para la página de pago
    this.routeDataService.setData(datoCompra);
    
    // Navegar a la página de pago
    this.router.navigate(['/client/pago']);
  }

  onClickEliminar = (item: any) => {
    const confirmation = this.mensaje.crearConfirmacion('¿Desea eliminar el registro?');
    confirmation.componentInstance.onSi.subscribe(() => {
      this.eliminar(item.id);
    });
  }

  eliminar = (id: any) => {
    this.mensaje.showLoading();
    this.carritoService.delete(id).subscribe({
      next: (res) => {
        this.mensaje.showMessageSuccess('Registro eliminado correctamente');
      },
      error: (err) => {
        this.mensaje.showMessageErrorObservable(err);
      },
      complete: () => {
        this.mensaje.closeLoading();
        this.inicializarData();
      }
    });
  }

  // Métodos para simular pagos (se moverán al PagoComponent)
  simularPagoYape = () => {
    this.mensaje.showMessageSuccess('Simulando pago con Yape/Plin...');
    setTimeout(() => {
      this.confirmarPago();
    }, 2000);
  }

  simularPagoVisa = () => {
    this.mensaje.showMessageSuccess('Simulando pago con Visa...');
    setTimeout(() => {
      this.confirmarPago();
    }, 3000);
  }

  // Procesar pago con datos de tarjeta (simulado)
  procesarPagoConTarjeta = () => {
    // Validación básica de campos
    if (!this.form.get('cardNumber')?.value || !this.form.get('cardNombre')?.value || !this.form.get('cardApellido')?.value || !this.form.get('cardCvv')?.value) {
      this.mensaje.showMessageError('Complete los datos de la tarjeta correctamente');
      return;
    }

    this.mensaje.showLoading();
    this.mensaje.showMessageSuccess('Procesando pago con tarjeta...');
    
    setTimeout(() => {
      this.mensaje.closeLoading();
      this.confirmarPago();
    }, 4000);
  }

  // Confirmar pago exitoso
  confirmarPago = () => {
    const user = this.authService.getInfoUsuario();

    let detalle: any[] = [];
    this.productos.forEach((item) => {
      detalle.push({
        productoId: item.producto.id,
        cantidad: item.cantidad,
        carritoCompraId: item.id,
      });
    });

    const dato = {
      usuarioId: user.id,
      metodoPagoId: this.form.get('metodo')?.value,
      detalleCompra: detalle,
    };

    this.save(dato);
  }

  save = (dato: any) => {
    this.mensaje.showLoading();
    this.compraService.save(dato).subscribe({
      next: (res) => {
        this.mensaje.showMessageSuccess('Compra realizada con éxito');
        // Resetear el contador cuando se completa la compra
        this.carritoCounterService.updateCount(0);
      },
      error: (err) => {
        this.mensaje.showMessageErrorObservable(err);
      },
      complete: () => {
        this.mensaje.closeLoading();
        this.initForm();
        this.inicializarData();
        // Navegar a la página de compras
        this.router.navigate(['/client/compras']);
      },
    });
  }
}