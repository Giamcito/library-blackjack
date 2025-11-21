export interface PageReplacementResult {
  fallos: number;
  pasos: string; // descripción multilinea
  tabla: number[][]; // estado de frames por paso
}

function sanitizeReferencias(input: string): string {
  return input.replace(/\s+/g, "");
}

function ensureOnlyDigits(refs: string): string {
  const clean = sanitizeReferencias(refs);
  if (!/^\d+$/.test(clean)) {
    throw new Error("La cadena de referencias solo debe contener dígitos (0-9)");
  }
  return clean;
}

function parseReferences(refs: string): number[] {
  const clean = ensureOnlyDigits(refs);
  return clean.split("").map((c) => Number(c));
}

function initTable(pasos: number, marcos: number): number[][] {
  return Array.from({ length: pasos }, () => Array.from({ length: marcos }, () => -1));
}

function snapshot(frames: number[]): number[] {
  return [...frames];
}

export function fifo(referencias: string, marcos: number): PageReplacementResult {
  const refs = parseReferences(referencias);
  const pasos = refs.length;
  const tabla = initTable(pasos, marcos);
  const frames = Array.from({ length: marcos }, () => -1);
  let fifoPtr = 0;
  let fallos = 0;
  const descripcion: string[] = [];

  refs.forEach((r, i) => {
    const hit = frames.includes(r);
    if (!hit) {
      fallos++;
      frames[fifoPtr] = r;
      fifoPtr = (fifoPtr + 1) % marcos;
    }
    tabla[i] = snapshot(frames);
    descripcion.push(`Paso ${i + 1}: referencia ${r} => ${hit ? "HIT" : "FALLO"} | Frames: [${frames.map(f => f === -1 ? '-' : f).join(', ')}]`);
  });

  return { fallos, pasos: descripcion.join("\n"), tabla };
}

export function lru(referencias: string, marcos: number): PageReplacementResult {
  const refs = parseReferences(referencias);
  const pasos = refs.length;
  const tabla = initTable(pasos, marcos);
  const frames = Array.from({ length: marcos }, () => -1);
  const lastUsed = Array.from({ length: marcos }, () => -1);
  let fallos = 0;
  const descripcion: string[] = [];

  refs.forEach((r, i) => {
    let idx = frames.indexOf(r);
    let hit = idx !== -1;
    if (hit) {
      lastUsed[idx] = i;
    } else {
      fallos++;
      const empty = frames.indexOf(-1);
      if (empty !== -1) {
        frames[empty] = r;
        lastUsed[empty] = i;
      } else {
        // find LRU
        let lruIdx = 0;
        let min = Infinity;
        for (let j = 0; j < marcos; j++) {
          if (lastUsed[j] < min) {
            min = lastUsed[j];
            lruIdx = j;
          }
        }
        frames[lruIdx] = r;
        lastUsed[lruIdx] = i;
      }
    }
    tabla[i] = snapshot(frames);
    descripcion.push(`Paso ${i + 1}: referencia ${r} => ${hit ? "HIT" : "FALLO"} | Frames: [${frames.map(f => f === -1 ? '-' : f).join(', ')}]`);
  });

  return { fallos, pasos: descripcion.join("\n"), tabla };
}

export function optimo(referencias: string, marcos: number): PageReplacementResult {
  const refs = parseReferences(referencias);
  const pasos = refs.length;
  const tabla = initTable(pasos, marcos);
  const frames = Array.from({ length: marcos }, () => -1);
  let fallos = 0;
  const descripcion: string[] = [];

  function nextUseIndex(value: number, start: number): number {
    for (let i = start; i < refs.length; i++) if (refs[i] === value) return i;
    return -1;
  }

  refs.forEach((r, i) => {
    let idx = frames.indexOf(r);
    let hit = idx !== -1;
    if (!hit) {
      fallos++;
      const empty = frames.indexOf(-1);
      if (empty !== -1) {
        frames[empty] = r;
      } else {
        let replaceIdx = 0;
        let farthest = -1;
        for (let j = 0; j < marcos; j++) {
          const next = nextUseIndex(frames[j], i + 1);
          if (next === -1) {
            replaceIdx = j;
            farthest = Number.MAX_SAFE_INTEGER;
            break;
          }
          if (next > farthest) {
            farthest = next;
            replaceIdx = j;
          }
        }
        frames[replaceIdx] = r;
      }
    }
    tabla[i] = snapshot(frames);
    descripcion.push(`Paso ${i + 1}: referencia ${r} => ${hit ? "HIT" : "FALLO"} | Frames: [${frames.map(f => f === -1 ? '-' : f).join(', ')}]`);
  });

  return { fallos, pasos: descripcion.join("\n"), tabla };
}
