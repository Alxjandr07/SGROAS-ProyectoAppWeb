export interface Paged<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ProvinciaAbd {
  idProvincia: number;
  nombre: string;
}

export interface CiudadAbd {
  idCiudad: number;
  nombre: string;
  idProvincia: number;
  nombreProvincia: string;
}

export interface TerminalAbd {
  idTerminal: number;
  nombre: string;
  idCiudad: number;
  nombreCiudad: string;
}

export interface RolAbd {
  idRol: number;
  nombre: string;
  descripcion: string;
}

export interface CatalogosAbd {
  provincias: ProvinciaAbd[];
  ciudades: CiudadAbd[];
  terminales: TerminalAbd[];
  roles: RolAbd[];
}

export interface RutaAbdRequest {
  idTerminalOrigen: number;
  idTerminalDestino: number;
  precioPasaje: number;
}

export interface RutaAbd {
  idRuta: number;
  idTerminalOrigen: number;
  terminalOrigen: string;
  idTerminalDestino: number;
  terminalDestino: string;
  precioPasaje: number;
  totalProgramaciones: number;
}

export interface UnidadAbdRequest {
  placa: string;
  numeroDisco: string;
  modelo: string;
  capacidad: number;
  anioFabricacion?: number | null;
  estado: string;
}

export interface UnidadAbd {
  idUnidad: number;
  placa: string;
  numeroDisco: string;
  modelo: string;
  capacidad: number;
  anioFabricacion: number | null;
  estado: string;
}

export interface ProgramacionAbdRequest {
  fecha: string;
  horaSalida: string;
  horaEstimadaLlegada: string;
  estado: string;
  idRuta: number;
  idUnidad: number;
  idConductor: number;
}

export interface ProgramacionAbd {
  idProgramacion: number;
  fecha: string;
  horaSalida: string;
  horaEstimadaLlegada: string;
  estado: string;
  idRuta: number;
  rutaDescripcion: string;
  idUnidad: number;
  unidadPlaca: string;
  idConductor: number;
  conductorNombres: string;
}

export interface IncidenteAbdRequest {
  tipo: string;
  descripcion: string;
  nivelSugerido: string;
  evidencia?: string;
  estado: string;
  idUnidad: number;
}

export interface IncidenteAbd {
  idIncidente: number;
  tipo: string;
  descripcion: string;
  nivelSugerido: string;
  fechaIncidente: string;
  evidencia: string | null;
  estado: string;
  idUnidad: number;
  unidadPlaca: string;
}

export interface AlertaAbd {
  idAlerta: number;
  nivelRiesgo: string;
  descripcion: string;
  fecha: string;
  idIncidente: number;
  incidenteTipo: string;
}

export interface ConteoAbd {
  clave: string;
  total: number;
}

export interface TopRutaAbd {
  idRuta: number;
  descripcion: string;
  totalProgramaciones: number;
}

export interface ItemGrafico {
  etiqueta: string;
  valor: number;
  color?: string;
}

export interface ResumenAbd {
  totalProgramaciones: number;
  programacionesActivas: number;
  totalIncidentes: number;
  incidentesAltoNivel: number;
  totalAlertas: number;
  totalUnidades: number;
  unidadesEnMantenimiento: number;
  totalRutas: number;
}
