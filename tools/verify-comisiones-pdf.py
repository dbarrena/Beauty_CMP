"""Validate the sample and pagination fixtures after running the Kotlin generator."""
from pathlib import Path
import re
import pdfplumber
from pypdf import PdfReader

sample = Path('output/pdf/Lasso_Comisiones_Ejemplo.pdf')
reader = PdfReader(sample, strict=True)
assert len(reader.pages) == 1
text = reader.pages[0].extract_text()
for expected in ('María Fernanda López', '$4,100.00', '$3,735.00', '$365.00',
                 'Aceite protector térmico', '12 oct 2026 al 18 oct 2026'):
    assert expected in text, expected
assert sample.stat().st_size < 1_000_000
for file in (sample, Path('tmp/pdfs/commissions-pagination.pdf'), Path('tmp/pdfs/commissions-empty.pdf')):
    doc = PdfReader(file, strict=True)
    with pdfplumber.open(file) as pdf:
        for index, page in enumerate(pdf.pages):
            assert all(35 <= c['x0'] <= c['x1'] <= 560 and 25 <= c['top'] <= c['bottom'] <= 830 for c in page.chars), (file, index)
    for index, page in enumerate(doc.pages):
        assert f'{index+1} / {len(doc.pages)}' in page.extract_text()
        for font in page['/Resources']['/Font'].values():
            obj = font.get_object()
            assert '/ToUnicode' in obj
            assert '/FontFile2' in obj['/DescendantFonts'][0].get_object()['/FontDescriptor']
    print(f'{file}: {len(doc.pages)} pages; embedded fonts, page numbers and text bounds verified')
stress_text = '\n'.join(p.extract_text() for p in PdfReader('tmp/pdfs/commissions-pagination.pdf').pages)
assert set(map(int, re.findall(r'Servicio (\d+) -', stress_text))) == set(range(1, 66))
assert 'Variable' in stress_text
assert stress_text.count('protección') == 125 and stress_text.count('teñido') == 125
empty_text = PdfReader('tmp/pdfs/commissions-empty.pdf').pages[0].extract_text()
assert 'Sin servicios en este periodo.' in empty_text and 'Sin productos en este periodo.' in empty_text
print('All commission PDF checks passed.')
