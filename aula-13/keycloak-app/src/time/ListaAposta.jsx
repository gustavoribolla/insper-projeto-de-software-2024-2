import { useKeycloak } from "@react-keycloak/web";
import { useEffect, useState } from "react";

function ListaAposta() {
    const { keycloak, initialized } = useKeycloak();
    const [data, setData] = useState([]);

    useEffect(() => {
        if (initialized && keycloak.authenticated) {
          fetch('http://localhost:8081/aposta', {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${keycloak.token}`, // Adiciona o token ao cabeçalho
            },
          })
          .then(response => response.json())
          .then(data => setData(data))
          .catch(error => console.error("Erro ao buscar apostas:", error));
        }
    }, [initialized, keycloak]);

    return (
        <div>
            <h1>Lista de Apostas</h1>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>ID Partida</th>
                        <th>Data da Aposta</th>
                        <th>Resultado</th>
                        <th>Valor</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    {data.map((aposta, index) => (
                        <tr key={index}>
                            <td>{aposta.id}</td>
                            <td>{aposta.idPartida}</td>
                            <td>{new Date(aposta.dataAposta).toLocaleString()}</td>
                            <td>{aposta.resultado}</td>
                            <td>{aposta.valor.toFixed(2)}</td>
                            <td>{aposta.status}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default ListaAposta;
